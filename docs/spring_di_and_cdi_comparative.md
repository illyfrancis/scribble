[Original](http://mobiarch.wordpress.com/2013/01/11/spring-di-and-cdi-comparative-study/)

# Spring DI and CDI Comparative Study

This article shows how to perform basic Dependency Injection (DI) using Spring 3.2 and Context and Dependency Injection (CDI) of Java EE 6. Basically, this will help someone quickly learn one of the technologies when she knows the other.

Note: For Spring, we assume that the application is Spring MVC based. Also, for the sake of brevity I don’t create interfaces for Spring beans.

## Basic Setup

For CDI, a file called beans.xml must be present in WEB-INF folder of a WAR file or META-INF folder of a JAR file. The file may be completely empty. If this file is absent, CDI is disabled at runtime. This is a common rookie mistake.

In a Spring MVC application, specify the location of the configuration file in web.xml as a part of the Spring dispatcher servlet definition. It is common to break up configuration in multiple files. For example:

    <servlet>
    <servlet-name>spring-servlet</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
     <param-name>contextConfigLocation</param-name>
     <param-value>/WEB-INF/spring-mvc.xml,/WEB-INF/applicationContext.xml</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
    </servlet>

To enable annotation based DI, the configuration file must specify the Java packages that will be scanned for injection points. You need to specify the parent package only. Spring will scan all child packages. For example:

    <beans xmlns="http://www.springframework.org/schema/beans"
      xmlns:context="http://www.springframework.org/schema/context" ...>
      <context:component-scan base-package="com.my.spring,com.other.package"/>
    </beans>

In contrast, CDI will scan all classes at deployment time for injection points. Both will undeploy the application if an injection point can not be resolved for whatever reason.

## Defining a Managed Bean

A bean must be managed by a DI container for all the magic (like injection and method interception) to happen.

In CDI, any POJO class can be managed without any modification. For example:

    public class MyClass {
        public void doIt() {
        }
    }

Use the @Named annotation only if you must give the class an EL name. This is something we need to do for JSF controllers so that we can refer to it from a XHTML view file.

    @Named
    public class MyClass {
        public void doIt() {
        }
    }

In addition to above, a few classes are implicitly managed:

- All EJB business classes (annotated with @Stateless, @Stateful etc) are also managed beans without the need to use the @Named annotation.
- JSF controller classes can be annotated with either @Named or @ManagedBean. In either case, they are managed by CDI.
- Servlets are implicitly managed by CDI.

In Spring, use @Component or one of its derivatives like @Controller, @Service and @Repository to make a class managed by DI. It is recommended that you use the appropriate specialized annotations for added benefits. For example, for a Spring MVC controller, use @Controller. Example:

    @Controller
    public class MyClass{
    }

    @Component
    public class AnotherClass {
    }

## Injecting a Managed Bean

For both Spring and CDI, the class where injection is performed must itself be a managed bean. In other words, both the bean being injected and the bean receiving the injection are managed beans.

In CDI, use @Inject to perform injection (define an injection point).

    @Named
    public class MyClass {
      @Inject
      AnotherClass obj;
    }

    public class AnotherClass {
    }

In the example above, we used @Named with MyClass because we intend to use it as a JSF controller. Where as, AnotherClass has no special decoration and can still be managed and injected by CDI.

In addition to @Inject, you can use @EJB to inject EJB and @Resource to inject resources like data source. @EJB and @Resource become necessary when you must refer to the injected item by some kind of a string name.

In Spring, use the @Autowired annotation to define an injection point.

    @Controller
    public class MyClass {
      @Autowired
      AnotherClass obj;
    }

    @Component
    public class AnotherClass{
    }

What is described above is field level injection. Instances can be injected in many other places, like in method parameter and constructor. We will not discuss them here.

## Bean Lifecycle Events

Both Spring and CDI use the @PostConstruct and @PreDestroy annotations to mark lifecycle callback methods. A @PostConstruct method is called after all the injection points have been satisfied. This happens sometime after the costructor and as a result a better place to perform initialization than the constructor. @PreDestroy is called when a bean’s scope is about to be destroyed. For example, if a bean is in session scope and the session is about to expire.

    CDI example:

    public class AnotherClass{
        @PostConstruct
        public void init() {
        }
    }

Spring Example:

    @Component
    public class AnotherClass{
        @PostConstruct
        public void init() {
        }
    }

## Bean Scope

In CDI, you can put a bean in dependent, request, conversation, session, and application scopes. The default is dependent scope (except JSF controllers are in request scope by default). With dependent scope, a new instance is created to satisfy every injection point. The remaining scopes have a larger lifespan. For example a bean in session scope is created only once per user session. The bean is destroyed when the session expires. CDI provides an annotation for each scope. For example, the following sets the scope of a managed bean to request.

    @Named
    @RequestScoped
    public class AnotherClass {
    }

CDI gives complete flexibility about mixing of scopes. That means, a bean of any scope can inject another bean in any other scope. For example, a session scoped bean can inject a request scoped bean.

    @Named
    @SessionScoped
    public class MyClass {
        @Inject
        AnotherClass obj;
    }

Spring works in a very similar way. But, be aware of subtle differences. First, let’s map the CDI scopes with Spring.

    CDI Scope            Spring Scope
    =================================
    Dependent (default)  Prototype
    Request              Request
    Conversation         (No equivalent exists)
    Session              Session
    Application          Singleton (default)

The default scope in Spring is singleton.

Spring provides the @Scope annotation to set the scope of a bean. But, it’s usage is less straightforward and you need to be aware of a few things. The scope of a @Controller is fixed as a singleton and you can’t change it. A single instance of a controller is used to serve all requests from any user. So, be aware of thread safety and information leakage across users.

Spring supports mixing of scopes. For example, a controller that is always a singleton can inject a request scoped bean. But, this takes a little extra work.

    @Controller
    public class MyClass {
      @Autowired
      AnotherClass obj;
    }
    @Component
    @Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
    public class AnotherClass {
    }

The extra work I was talking about is setting the proxyMode attribute. With proxy enabled, Spring wraps the actual bean instance in a dynamic proxy. This is necessary for injecting a bean with a shorter lifespan (request) in a longer lifespan (singleton) bean.

In the example above, the injected instance of AnotherClass is created in the request scope. As a result, it is safe as a member variable of the controller which itself is a singleton. This is not your usual Java programming and takes a little getting used to. The same magic happens in CDI, which uses proxy in all cases, except for dependent scope. Basically, with a proxy, a bean is instantiated lazily on demand. Consider a method of the controller:

    @Controller
    public class MyClass {
      @Autowired
      AnotherClass obj;

      public String doIt() {
          System.out.println("Controller doing it");
          obj.doSomething(); //Injected instance created here
      }
    }

An instance of AnotherClass is created very late just prior to calling the doSomething() method. Once again, this instance is created in request scope and completely distinct for every request. So, the code above is completely safe without any worry for thread safety or information leakage between users. The principles are exactly the same for CDI. Except, there, proxy is always used and there is no need to set a proxyMode type flag anywhere.

Proxy mode does not need to be set when a bean injects another bean that has the same or longer lifespan. For example, a bean in request scope can inject a bean in session scope without setting the proxy.

    @Component
    @Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
    public class AnotherClass {
        @Autowired
        ClassB obj;
    }
    @Component
    @Scope(value = "session")
    public class ClassB {
    }

## Alternative Bean Resolution

One of the biggest benefits of DI is that you can switch the implementation of a bean throughout an application without making mass changes. For example, we can switch a data access class that fetches data from a  SQL database to another class that uses Web Service. The basic mechanism is same for Spring and CDI. Your application contains multiple classes that are candidates for injection at an injection point. Then you use some kind of configuration to narrow down the choice to a single candidate.

There are several ways of doing alternative bean resolution in CDI. One way is to disable a non-candidate bean using @Alternative.

    public interface MyService {
    	public void doIt();
    }

    @Alternative //Disable this class
    public class ServiceA implements MyService {
        @Override
        public void doIt() {
        }
    }

    public class ServiceB implements MyService {
        @Override
        public void doIt() {
        }
    }

    @Named
    public class HomeController {
    	@Inject
    	MyService obj; //ServiceB will be injected
    }
    In Spring, mark the preferred injection candidate with @Primary.

    public interface MyService {
    	public void doIt();
    }

    @Component
    public class ServiceA implements MyService {
        @Override
        public void doIt() {
        }
    }

    @Primary
    @Component
    public class ServiceB implements MyService {
        @Override
        public void doIt() {
        }
    }

    @Controller
    public class HomeController {
    	@Autowired
    	MyService obj; //ServiceB will be injected
    }

## Custom Bean Creation

By default, the DI container (Spring or CDI) is responsible for instantiating an injected bean instance. It uses the zero argument constructor to create the object. Some classes may not have a zero arg constructor or may need special initialization sequence. These beans can not be managed by the container. The solution is to provide one’s own bean creation routine.

In CDI, this is done using a producer method. A producer method returns an instance of a bean. It becomes the source of the bean at an injection point. To avoid ambiguity, we must declare the bean itself as an alternative.

    @Alternative
    public class Person {
      private String name;
      public String getName() {
        return name;
      }
      public void setName(String name) {
        this.name = name;
      }
      public Person(String name) {
        super();
        this.name = name;
      }
    }

Then, we can define a producer class that acts as a factory.

    public class PersonFactory {
      @Produces
      @RequestScoped
      public Person createPerson() {
        return new Person("Daffy Duck");
      }
    }

Note, you can set the scope of the bean. Similarly, if you need to give the bean an EL name, you can use @Named.

The bean can be injected as usual:

    @Named
    public class MyClass {
        @Inject
        Person person;
    }

Now, at the point of injection, the createPerson() method will be called. If we did not designate the Person class as an alternative, there will be two potential sources for the bean’s instance. This leads to ambiguity and error during deployment.

In Spring, if a bean is created by a factory class, there is no need to designate the bean class using @Component.

    public class Person {
      private String name;
      public String getName() {
        return name;
      }
      public void setName(String name) {
        this.name = name;
      }
      public Person(String name) {
        super();
        this.name = name;
      }
    }

The factory class and its producer method is declared as follows.

    @Configuration
    public class PersonFactory {
    	@Bean
    	@Scope(value = "request")
    	public Person createPerson() {
    		return new Person("Daffy Duck");
    	}
    }

Note, just as in CDI, you can set the scope with the producer method.

## Summary

Spring and CDI are both very capable DI containers. As we have seen here, they work in a very similar manner. Unfortunately, bulk of Spring documentation still uses the old XML syntax. Finding information about annotation based processing is unusually difficult. Hopefully, this article will be useful for someone who want to do apples to apples comparison of the two technologies.