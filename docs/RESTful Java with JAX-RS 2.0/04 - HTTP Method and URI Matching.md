[toc]

# Chapter 4. HTTP Method and URI Matching

Now that we have a foundation in JAX-RS, it’s time to start looking into the details. In Chapter 3, you saw how we used the @GET, @PUT, @POST, and @DELETE annotations to bind Java methods to a specific HTTP operation. You also saw how we used the `@Path` annotation to bind a URI pattern to a Java method. While applying these annotations seems pretty straightforward, there are some interesting attributes that we’re going to examine within this chapter.

## Binding HTTP Methods

JAX-RS defines five annotations that map to specific HTTP operations:

- @javax.ws.rs.GET
- @javax.ws.rs.PUT
- @javax.ws.rs.POST
- @javax.ws.rs.DELETE
- @javax.ws.rs.HEAD

In Chapter 3, we used these annotations to bind HTTP GET requests to a specific Java method. For example:

	@Path("/customers")
	public class CustomerService {

	   @GET
	   @Produces("application/xml")
	   public String getAllCustomers() {
	   }
	}

Here we have a simple method, `getAllCustomers()`. The `@GET` annotation instructs the JAX-RS runtime that this Java method will process HTTP GET requests to the URI `/customers`. You would use one of the other five annotations described earlier to bind to different HTTP operations. One thing to note, though, is that you may only apply one HTTP method annotation per Java method. A deployment error occurs if you apply more than one.

Beyond simple binding, there are some interesting things to note about the implementation of these types of annotations. Let’s take a look at @GET, for instance:

	package javax.ws.rs;

	import ...;

	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@HttpMethod(HttpMethod.GET)
	public @interface GET {
	}

`@GET`, by itself, does not mean anything special to the JAX-RS provider. In other words, JAX-RS is not hardcoded to look for this annotation when deciding whether or not to dispatch an HTTP GET request. What makes the @GET annotation meaningful to a JAX-RS provider is the meta-annotation `@javax.ws.rs.HttpMethod`. Meta-annotations are simply annotations that annotate other annotations. When the JAX-RS provider examines a Java method, it looks for any method annotations that use the meta-annotation @HttpMethod. The value of this meta-annotation is the actual HTTP operation that you want your Java method to bind to.

### HTTP Method Extensions

What are the implications of this? This means that you can create new annotations that bind to HTTP methods other than GET, POST, DELETE, HEAD, and PUT. While HTTP is a ubiquitous, stable protocol, it is still constantly evolving. For example, consider the WebDAV standard.[3] The WebDAV protocol makes the Web an interactive readable and writable medium. It allows users to create, change, and move documents on web servers. It does this by adding a bunch of new methods to HTTP like MOVE, COPY, MKCOL, LOCK, and UNLOCK.

Although JAX-RS does not define any WebDAV-specific annotations, we could create them ourselves using the @HttpMethod annotation:

	package org.rest.webdav;

	import ...;

	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@HttpMethod("LOCK")
	public @interface LOCK {
	}

Here, we have defined a new `@org.rest.LOCK` annotation using `@HttpMethod` to specify the HTTP operation it binds to. We can then use it on JAX-RS resource methods:

	@Path("/customers")
	public class CustomerResource {

	   @Path("{id}")
	   @LOCK
	   public void lockIt(@PathParam("id") String id) {
	      ...
	   }
	}

Now WebDAV clients can invoke `LOCK` operations on our web server and they will be dispatched to the `lockIt()` method.

>	**WARNING**
>
>	Do not use @HttpMethod to define your own application-specific HTTP methods. @HttpMethod exists to hook into new methods defined by standards bodies like the W3C. The purpose of the uniform interface is to define a set of well-known behaviors across companies and organizations on the Web. Defining your own methods breaks this architectural principle.

## @Path

There’s more to the `@javax.ws.rs.Path` annotation than what we saw in our simple example in Chapter 3. `@Path` can have complex matching expressions so that you can be more specific about what requests get bound to which incoming URIs. `@Path` can also be used on a Java method as sort of an object factory for subresources of your application. We’ll examine both in this section.

### Binding URIs

The `@javax.ws.rs.Path` annotation in JAX-RS is used to define a URI matching pattern for incoming HTTP requests. It can be placed upon a class or on one or more Java methods. For a Java class to be eligible to receive any HTTP requests, the class must be annotated with at least the `@Path("/")` expression. These types of classes are called JAX-RS *root resources*.

The value of the `@Path` annotation is an expression that denotes a relative URI to the context root of your JAX-RS application. For example, if you are deploying into a WAR archive of a servlet container, that WAR will have a base URI that browsers and remote clients use to access it. `@Path` expressions are relative to this URI.

To receive a request, a Java method must have at least an HTTP method annotation like `@javax.ws.rs.GET` applied to it. This method is not required to have an `@Path` annotation on it, though. For example:

	@Path("/orders")
	public class OrderResource {
	   @GET
	   public String getAllOrders() {
	       ...
	   }
	}

An HTTP request of `GET /orders` would dispatch to the `getAllOrders()` method.

You can also apply `@Path` to your Java method. If you do this, the URI matching pattern is a concatenation of the class’s `@Path` expression and that of the method’s. For example:

	@Path("/orders")
	public class OrderResource {

	   @GET
	   @Path("unpaid")
	   public String getUnpaidOrders() {
	      ...
	   }
	}

So, the URI pattern for `getUnpaidOrders()` would be the relative URI `/orders/unpaid`.

### @Path Expressions

The value of the `@Path` annotation is usually a simple string, but you can also define more complex expressions to satisfy your URI matching needs.

#### Template parameters

In Chapter 3, we wrote a customer access service that allowed us to query for a specific customer using a wildcard URI pattern:

	@Path("/customers")
	public class CustomerResource {

	   @GET
	   @Path("{id}")
	   public String getCustomer(@PathParam("id") int id) {
	      ...
	   }
	}

These template parameters can be embedded anywhere within an `@Path` declaration. For example:

	@Path("/")
	public class CustomerResource {

	   @GET
	   @Path("customers/{firstname}-{lastname}")
	   public String getCustomer(@PathParam("firstname") String first,
	                             @PathParam("lastname") String last) {
	      ...
	   }
	}

In our example, the URI is constructed with a customer’s first name, followed by a hyphen, ending with the customer’s last name. So, the request `GET /customers/333` would no longer match to getCustomer(), but a `GET/customers/bill-burke` request would.

#### Regular expressions

`@Path` expressions are not limited to simple wildcard matching expressions. For example, our `getCustomer()` method takes an integer parameter. We can change our `@Path` value to match only digits:

	@Path("/customers")
	public class CustomerResource {

	   @GET
	   @Path("{id : \\d+}")
	   public String getCustomer(@PathParam("id") int id) {
	      ...
	   }
	}

Regular expressions are not limited in matching one segment of a URI. For example:

	@Path("/customers")
	public class CustomerResource {

	   @GET
	   @Path("{id : .+}")
	   public String getCustomer(@PathParam("id") String id) {
	      ...
	   }

	   @GET
	   @Path("{id : .+}/address")
	   public String getAddress(@PathParam("id") String id) {
	      ...
	   }

	}

We’ve changed `getCustomer()`’s `@Path` expression to `{id : .+}`. The `.+` is a regular expression that will match any stream of characters after `/customers`. So, the `GET /customers/bill/burke` request would be routed to `getCustomer()`.

The `getAddress()` method has a more specific expression. It will map any stream of characters after `/customers` that ends with `/address`. So, the `GET /customers/bill/burke/address` request would be routed to the `getAddress()` method.

#### Precedence rules

You may have noticed that, together, the `@Path` expressions for `getCustomer()` and `getAddress()` are ambiguous. A `GET /customers/bill/burke/address` request could match either `getCustomer()` or `getAddress()`, depending on which expression was matched first by the JAX-RS provider. The JAX-RS specification has defined strict sorting and precedence rules for matching URI expressions and is based on a most specific match wins algorithm. The JAX-RS provider gathers up the set of deployed URI expressions and sorts them based on the following logic:

1. The primary key of the sort is the number of literal characters in the full URI matching pattern. The sort is in descending order. In our ambiguous example, `getCustomer()`’s pattern has 11 literal characters: `/customers/`. The `getAddress()` method’s pattern has 18 literal characters: `/customers/` plus `address`. Therefore, the JAX-RS provider will try to match `getAddress()`’s pattern before `getCustomer()`.

2. The secondary key of the sort is the number of template expressions embedded within the pattern—that is, `{id}` or `{id : .+}`. This sort is in descending order.

3. The tertiary key of the sort is the number of nondefault template expressions. A default template expression is one that does not define a regular expression—that is, `{id}`.

Let’s look at a list of sorted URI matching expressions and explain why one would match over another:

	1 /customers/{id}/{name}/address
	2 /customers/{id : .+}/address
	3 /customers/{id}/address
	4 /customers/{id : .+}

Expressions 1–3 come first because they all have more literal characters than expression 4. Although expressions 1–3 all have the same number of literal characters, expression 1 comes first because sorting rule #2 is triggered. It has more template expressions than either pattern 2 or 3. Expressions 2 and 3 have the same number of literal characters and same number of template expressions. Expression 2 is sorted ahead of 3 because it triggers sorting rule #3; it has a template pattern that is a regular expression.

These sorting rules are not perfect. It is still possible to have ambiguities, but the rules cover 90% of use cases. If your application has URI matching ambiguities, your application design is probably too complicated and you need to revisit and refactor your URI scheme.

#### Encoding

The URI specification only allows certain characters within a URI string. It also reserves certain characters for its own specific use. In other words, you cannot use these characters as part of your URI segments. This is the set of allowable and reserved characters:

- The US-ASCII alphabetic characters `a–z` and `A–Z` are allowable.
- The decimal digit characters `0–9` are allowable.
- All these other characters are allowable: `_-!.~'()*`.
- These characters are allowed but are reserved for URI syntax: `,;:$&+=?/\[]@`.

All other characters must be encoded using the “%” character followed by a two-digit hexadecimal number. This hexadecimal number corresponds to the equivalent hexadecimal character in the ASCII table. So, the string `bill&burke` would be encoded as `bill_burke`.

When creating `@Path` expressions, you may encode its string, but you do not have to. If a character in your `@Path` pattern is an illegal character, the JAX-RS provider will automatically encode the pattern before trying to match and dispatch incoming HTTP requests. If you do have an encoding within your `@Path` expression, the JAX-RS provider will leave it alone and treat it as an encoding when doing its request dispatching. For example:

	@Path("/customers"
	public class CustomerResource {

	   @GET
	   @Path("roy&fielding")
	   public String getOurBestCustomer() {
	      ...
	   }
	}

The `@Path` expression for `getOurBestCustomer()` would match incoming requests like `GET /customers/roy%26fielding`.

### Matrix Parameters

One part of the URI specification that we have not touched on yet is *matrix parameters*. Matrix parameters are name-value pairs embedded within the path of a URI string. For example:

	http://example.cars.com/mercedes/e55;
	color=black/2006

They come after a URI segment and are delimited by the “;” character. The matrix parameter in this example comes after the URI segment e55. Its name is color and its value is black. Matrix parameters are different than query parameters, as they represent attributes of certain segments of the URI and are used for identification purposes. Think of them as adjectives. Query parameters, on the other hand, always come at the end of the URI and always pertain to the full resource you are referencing.

Matrix parameters are ignored when matching incoming requests to JAX-RS resource methods. It is actually illegal to specify matrix parameters within an `@Path` expression. For example:

	@Path("/mercedes")
	public class MercedesService {

	   @GET
	   @Path("/e55/{year}")
	   @Produces("image/jpeg")
	   public Jpeg getE55Picture(@PathParam("year") String year) {
	     ...
	   }

If we queried our JAX-RS service with `GET /mercedes/e55;color=black/2006`, the `getE55Picture()` method would match the incoming request and would be invoked. Matrix parameters are not considered part of the matching process because they are usually variable attributes of the request. We’ll see in Chapter 5 how to access matrix parameter information within our JAX-RS resource methods.

## Subresource Locators

So far, I’ve shown you the JAX-RS capability to statically bind URI patterns expressed through the @Path annotation to a specific Java method. JAX-RS also allows you to dynamically dispatch requests yourself through subresource locators. Subresource locators are Java methods annotated with @Path, but with no HTTP method annotation, like @GET, applied to them. This type of method returns an object that is, itself, a JAX-RS annotated service that knows how to dispatch the remainder of the request. This is best described using an example.

Let’s continue by expanding our customer database JAX-RS service. This example will be a bit contrived, so please bear with me. Let’s say our customer database is partitioned into different databases based on geographic regions. We want to add this information to our URI scheme, but we want to decouple finding a database server from querying and formatting customer information. We will now add the database partition information to the URI pattern `/customers/{database}-db/{customerId}`. We can define a `CustomerDatabaseResource` class and have it delegate to our original `CustomerResource` class. Here’s the example:

	@Path("/customers")
	public class CustomerDatabaseResource {

	   @Path("{database}-db")
	   public CustomerResource getDatabase(@PathParam("database") String db) {
	      // find the instance based on the db parameter
	      CustomerResource resource = locateCustomerResource(db);
	      return resource;
	   }

	   protected CustomerResource locateCustomerResource(String db) {
	     ...
	   }
	}

The `CustomerDatabaseResource` class is our root resource. It does not service any HTTP requests directly. It processes the database identifier part of the URI and locates the identified customer database. Once it does this, it allocates a `CustomerResource` instance, passing in a reference to the database. The JAX-RS provider uses this `CustomerResource` instance to service the remainder of the request:

	public class CustomerResource {
	   private Map<Integer, Customer> customerDB =
	                          new ConcurrentHashMap<Integer, Customer>();
	   private AtomicInteger idCounter = new AtomicInteger();

	   public CustomerResource(Map<Integer, Customer> customerDB)
	   {
	      this.customerDB = customerDB;
	   }

	   @POST
	   @Consumes("application/xml")
	   public Response createCustomer(InputStream is) {
	     ...
	   }

	   @GET
	   @Path("{id}")
	   @Produces("application/xml")
	   public StreamingOutput getCustomer(@PathParam("id") int id) {
	     ...
	   }

	   @PUT
	   @Path("{id}")
	   @Consumes("application/xml")
	   public void updateCustomer(@PathParam("id") int id, InputStream is) {
	      ...
	   }
	}

So, if a client sends `GET /customers/northamerica-db/333`, the JAX-RS provider will first match the expression on the method `CustomerDatabaseResource.getDatabase()`. It will then match and process the remaining part of the request with the method `CustomerResource.getCustomer()`.

Besides the added constructor, another difference in the `CustomerResource` class from previous examples is that it is no longer annotated with `@Path`. It is no longer a *root resource* in our system; it is a *subresource* and must not be registered with the JAX-RS runtime within an Application class.

### Full Dynamic Dispatching

While our previous example does illustrate the concept of subresource locators, it does not show their full dynamic nature. The `CustomerDatabaseResource.getDatabase()` method can return any instance of any class. At runtime, the JAX-RS provider will introspect this instance’s class for resource methods that can handle the request.

Let’s say that in our example, we have two customer databases with different kinds of identifiers. One database uses a numeric key, as we talked about before. The other uses first and last name as a composite key. We would need to have two different classes to extract the appropriate information from the URI. Let’s change our example:

	@Path("/customers")
	public class CustomerDatabaseResource {

	   protected CustomerResource europe = new CustomerResource();
	   protected FirstLastCustomerResource northamerica =
	                              new FirstLastCustomerResource();

	   @Path("{database}-db")
	   public Object getDatabase(@PathParam("database") String db) {
	      if (db.equals("europe")) {
	          return europe;
	      }
	      else if (db.equals("northamerica")) {
	          return northamerica;
	      }
	      else return null;
	   }
	}

Instead of our `getDatabase()` method returning a `CustomerResource`, it will return any `java.lang.Object`. JAX-RS will introspect the instance returned to figure out how to dispatch the request. For this example, if our database is europe, we will use our original `CustomerResource` class to service the remainder of the request. If our database is northamerica, we will use a new subresource class `FirstLastCustomerResource`:

	public class FirstLastCustomerResource {
	   private Map<String, Customer> customerDB =
	                          new ConcurrentHashMap<String, Customer>();

	   @GET
	   @Path("{first}-{last}")
	   @Produces("application/xml")
	   public StreamingOutput getCustomer(@PathParam("first") String firstName,
	                                      @PathParam("last") String lastName) {
	     ...
	   }

	   @PUT
	   @Path("{first}-{last}")
	   @Consumes("application/xml")
	   public void updateCustomer(@PathParam("first") String firstName,
	                              @PathParam("last") String lastName,
	                              InputStream is) {
	      ...
	   }
	}

Customer lookup requests routed to europe would match the `/customers/{database}-db/{id}` URI pattern defined in `CustomerResource`. Requests routed to northamerica would match the `/customers/{database}-db/{first}-{last}` URI pattern defined in `FirstLastCustomerResource`. This type of pattern gives you a lot of freedom to dispatch your own requests.

## Gotchas in Request Matching

There are some fine print details about the URI request matching algorithm that I must go over, as there may be cases where you’d expect a request to match and it doesn’t. First of all, the specification requires that potential JAX-RS class matches are filtered first based on the root `@Path` annotation. Consider the following two classes:

	@Path("/a")
	public class Resource1 {
	   @GET
	   @Path("/b")
	   public Response get() {}
	}

	@Path("/{any : .*}")
	public class Resource2 {

	   @GET
	   public Response get() {}

	   @OPTIONS
	   public Response options() {}
	}

If we have an HTTP request `GET /a/b`, the matching algorithm will first find the best class that matches before finishing the full dispatch. In this case, class `Resource1` is chosen because its `@Path("/a")` annotation best matches the initial part of the request URI. The matching algorithm then tries to match the remainder of the URI based on expressions contained in the `Resource1` class.

Here’s where the weirdness comes in. Let’s say you have the HTTP request `OPTIONS /a/b`. If you expect that the `Resource2.options()` method would be invoked, you would be wrong! You would actually get a 405, “Method Not Allowed,” error response from the server. This is because the initial part of the request path, `/a`, matches the `Resource1` class best, so `Resource1` is used to resolve the rest of the HTTP request. If we change `Resource2` as follows, the request would be processed by the `options()` method:

	@Path("/a")
	public class Resource2 {

	   @OPTIONS
	   @Path("b")
	   public Response options() {}
	}

If the @Path expressions are the same between two different JAX-RS classes, then they both are used for request matching.
There are also similar ambiguities in subresource locator matching. Take these classes, for example:

	@Path("/a")
	public class Foo {
	   @GET
	   @Path("b")
	   public String get() {...}

	   @Path("{id}")
	   public Locator locator() { return new Locator(); }
	}

	public class Locator{
	   @PUT
	   public void put() {...}
	}

If we did a `PUT /a/b` request, you would also get a 405 error response. The specification algorithm states that if there is at least one other resource method whose @Path expression matches, then no subresource locator will be traversed to match the request.

In most applications, you will not encounter these maching issues, but it’s good to know about them just in case you do. I tried to get these problems fixed in the JAX-RS 2.0 spec, but a few JSR members thought that this would break backward compatibility.

## Wrapping Up

In this chapter, we examined the intricacies of the `@javax.ws.rs.Path` annotation. `@Path` allows you to define complex URI matching patterns that can map to a Java method. These patterns can be defined using regular expressions and also support encoding. We also discussed subresource locators, which allow you to programmatically perform your own dynamic dispatching of HTTP requests. Finally, we looked at how you can hook into new HTTP operations by using the `@HttpMethod` annotation. You can test-drive the code in this chapter in Chapter 19.

- [3] For more information on WebDAV, see http://www.webdav.org.