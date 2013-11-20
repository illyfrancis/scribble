[toc]

# Chapter 6. JAX-RS Content Handlers

In the last chapter, we focused on injecting information from the header of an HTTP request. In this chapter, we will focus on the message body of an HTTP request and response. In the examples in previous chapters, we used low-level streaming to read in requests and write out responses. To make things easier, JAX-RS also allows you to marshal message bodies to and from specific Java types. It has a number of built-in providers, but you can also write and plug in your own providers. Let’s look at them all.

## Built-in Content Marshalling

JAX-RS has a bunch of built-in handlers that can marshal to and from a few different specific Java types. While most are low-level conversions, they can still be useful to your JAX-RS classes.

### javax.ws.rs.core.StreamingOutput

We were first introduced to `StreamingOutput` back in Chapter 3. `StreamingOutput` is a simple callback interface that you implement when you want to do raw streaming of response bodies:

	public interface StreamingOutput  {
	      void write(OutputStream output) throws IOException,
			                                    WebApplicationException;
	}

You allocate implemented instances of this interface and return them from your JAX-RS resource methods. When the JAX-RS runtime is ready to write the response body of the message, the `write()` method is invoked on the `StreamingOutput` instance. Let’s look at an example:

	@Path("/myservice")
	public class MyService {

	   @GET
	   @Produces("text/plain")
	   StreamingOutput get() {
	      return new StreamingOutput() {
	         public void write(OutputStream output) throws IOException,
	                                           WebApplicationException {
	             output.write("hello world".getBytes());
	         }
	      };
	   }

Here, we’re getting access to the raw `java.io.OutputStream` through the `write()` method and outputting a simple string to the stream. I like to use an anonymous inner class implementation of the StreamingOutput interface rather than creating a separate public class. Since the `StreamingOutput` interface is so tiny, I think it’s beneficial to keep the output logic embedded within the original JAX-RS resource method so that the code is easier to follow. Usually, you’re not going to reuse this logic in other methods, so it doesn’t make much sense to create a specific class.

You may be asking yourself, “Why not just inject an `OutputStream` directly? Why have a callback object to do streaming output?” That’s a good question! The reason for having a callback object is that it gives the JAX-RS implementation freedom to handle output however it wants. For performance reasons, it may sometimes be beneficial for the JAX-RS implementation to use a different thread other than the calling thread to output responses. More importantly, many JAX-RS implementations have an interceptor model that abstracts things out like automatic GZIP encoding or response caching. Streaming directly can usually bypass these architectural constructs. Finally, the Servlet 3.0 specification has introduced the idea of asynchronous responses. The callback model fits in very nicely with the idea of asynchronous HTTP within the Servlet 3.0 specification.

### java.io.InputStream, java.io.Reader

For reading request message bodies, you can use a raw `InputStream` or `Reader` for inputting any media type. For example:

	@Path("/")
	public class MyService {

	   @PUT
	   @Path("/stuff")
	   public void putStuff(InputStream is) {
	      byte[] bytes = readFromStream(is);
	      String input = new String(bytes);
	      System.out.println(input);
	   }

	   private byte[] readFromStream(InputStream stream)
	           throws IOException
	   {
	      ByteArrayOutputStream baos = new ByteArrayOutputStream();

	      byte[] buffer = new byte[1000];
	      int wasRead = 0;
	      do {
	         wasRead = stream.read(buffer);
	         if (wasRead > 0) {
	            baos.write(buffer, 0, wasRead);
	         }
	      } while (wasRead > −1);
	      return baos.toByteArray();
	   }

Here, we’re reading the full raw bytes of the `java.io.InputStream` available and using them to create a String that we output to the screen:

	   @PUT
	   @Path("/morestuff")
	   public void putMore(Reader reader) {
	      LineNumberReader lineReader = new LineNumberReader(reader);
	      do {
	         String line = lineReader.readLine();
	         if (line != null) System.out.println(line);
	      } while (line != null);
	   }

For this example, we’re creating a `java.io.LineNumberReader` that wraps our injected Reader object and prints out every line in the request body.

You are not limited to using `InputStream` and `Reader` for reading input request message bodies. You can also return these as response objects. For example:

	@Path("/file")
	public class FileService {

	   private static final String basePath = "...";
	   @GET
	   @Path("{filepath: .*}")
	   @Produces("text/plain")
	   public InputStream getFile(@PathParam("filepath") String path) {
	      FileInputStream is = new FileInputStream(basePath + path);
	      return is;
	   }

Here, we’re using an injected `@PathParam` to create a reference to a real file that exists on our disk. We create a `java.io.FileInputStream` based on this path and return it as our response body. The JAX-RS implementation will read from this input stream into a buffer and write it back out incrementally to the response output stream. We must specify the `@Produces` annotation so that the JAX-RS implementation knows how to set the `Content-Type` header.

### java.io.File

Instances of `java.io.File` can also be used for input and output of any media type. Here’s an example for returning a reference to a file on disk:

	@Path("/file")
	public class FileService {

	   private static final String basePath = "...";
	   @GET
	   @Path("{filepath: .*}")
	   @Produces("text/plain")
	   public File getFile(@PathParam("filepath") String path) {
	      return new File(basePath + path);
	   }

In this example, we’re using an injected `@PathParam` to create a reference to a real file that exists on our disk. We create a `java.io.File` based on this path and return it as our response body. The JAX-RS implementation will open up an `InputStream` based on this file reference and stream into a buffer that is written back incrementally to the response’s output stream. We must specify the `@Produces` annotation so that the JAX-RS implementation knows how to set the `Content-Type` header.

You can also inject `java.io.File` instances that represent the incoming request response body. For example:

	   @POST
	   @Path("/morestuff")
	   public void post(File file) {
	      Reader reader = new Reader(new FileInputStream(file));
	      LineNumberReader lineReader = new LineNumberReader(reader);
	      do {
	         String line = lineReader.readLine();
	         if (line != null) System.out.println(line);
	      } while (line != null);
	   }

The way this works is that the JAX-RS implementation creates a temporary file for input on disk. It reads from the network buffer and saves the bytes read into this temporary file. In our example, we create a `java.io.FileInputStream` from the `java.io.File` object that was injected by the JAX-RS runtime. We then use this input stream to create a `LineNumberReader` and output the posted data to the console.

### byte[]

A raw array of bytes can be used for the input and output of any media type. Here’s an example:
 
	@Path("/")
	public class MyService {

	   @GET
	   @Produces("text/plain")
	   public byte[] get() {
	     return "hello world".getBytes();
	   }

	   @POST
	   @Consumes("text/plain")
	   public void post(byte[] bytes) {
	      System.out.println(new String(bytes));
	   }
	}

For JAX-RS resource methods that return an array of bytes, you must specify the `@Produces` annotation so that JAX-RS knows what media to use to set the `Content-Type` header.

### String, char[]

Most of the data formats on the Internet are text based. JAX-RS can convert any text-based format to and from either a String or an array of characters. For example:

	@Path("/")
	public class MyService {

	   @GET
	   @Produces("application/xml")
	   public String get() {
	     return "<customer><name>Bill Burke</name></customer>";
	   }

	   @POST
	   @Consumes("text/plain")
	   public void post(String str) {
	      System.out.println(str);
	   }
	}

For JAX-RS resource methods that return a String or an array of characters, you must specify the `@Produces` annotation so that JAX-RS knows what media to use to set the `Content-Type` header.

The JAX-RS specification does require that implementations be sensitive to the character set specified by the `Content-Type` when creating an injected String. For example, here’s a client HTTP POST request that is sending some text data to our service:

	POST /data
	Content-Type: application/xml;charset=UTF-8

	<customer>...</customer>

The Content-Type of the request is application/xml, but it is also stating the character encoding is UTF-8. JAX-RS implementations will make sure that the created Java String is encoded as UTF-8 as well.

### MultivaluedMap<String, String> and Form Input

HTML forms are a common way to post data to web servers. Form data is encoded as the `application/x-www-form-urlencoded` media type. In Chapter 5, we saw how you can use the `@FormParam` annotation to inject individual form parameters from the request. You can also inject a `MultivaluedMap<String, String>` that represents all the form data sent with the request. For example:

	@Path("/")
	public class MyService {

	   @POST
	   @Consumes("application/x-www-form-urlencoded")
	   @Produces("application/x-www-form-urlencoded")
	   public MultivaluedMap<String,String> post(
	                              MultivaluedMap<String, String> form) {

	      return form;
	   }
	}

Here, our `post()` method accepts `POST` requests and receives a `MultivaluedMap<String, String>` containing all our form data. You may also return a `MultivaluedMap` of form data as your response. We do this in our example.

The JAX-RS specification does not say whether the injected `MultivaluedMap` should contain encoded strings or not. Most JAX-RS implementations will automatically decode the map’s string keys and values. If you want it encoded, you can use the `@javax.ws.rs.Encoded` annotation to notify the JAX-RS implementation that you want the data in its raw form.

### javax.xml.transform.Source

The `javax.xml.transform.Source` interface represents XML input or output. It is usually used to perform XSLT transformations on input documents. Here’s an example:

	@Path("/transform")
	public class TransformationService {

	   @POST
	   @Consumes("application/xml")
	   @Produces("application/xml")
	   public String post(Source source) {

	      javax.xml.transform.TransformerFactory tFactory =
	               javax.xml.transform.TransformerFactory.newInstance();

	      javax.xml.transform.Transformer transformer =
	           tFactory.newTransformer(
	             new javax.xml.transform.stream.StreamSource("foo.xsl"));

	      StringWriter writer = new StringWriter();
	      transformer.transform(source,
	              new javax.xml.transform.stream.StreamResult(writer));

	      return writer.toString();
	   }

In this example, we’re having JAX-RS inject a `javax.xml.transform.Source` instance that represents our request body and we’re transforming it using an XSLT transformation.

Except for JAXB, `javax.xml.transform.Source` is the only XML-based construct that the specification requires implementers to support. I find it a little strange that you can’t automatically inject and marshal org.w3c.dom.Document objects. This was probably just forgotten in the writing of the specification.

## JAXB

JAXB is an older Java specification and is not defined by JAX-RS. JAXB is an annotation framework that maps Java classes to XML and XML schema. It is extremely useful because instead of interacting with an abstract representation of an XML document, you can work with real Java objects that are closer to the domain you are modeling. JAX-RS has built-in support for JAXB, but before we review these handlers, let’s get a brief overview of the JAXB framework.

### Intro to JAXB

A whole book could be devoted to explaining the intricacies of JAXB, but I’m only going to focus here on the very basics of the framework. If you want to map an existing Java class to XML using JAXB, there are a few simple annotations you can use. Let’s look at an example:

	@XmlRootElement(name="customer")
	@XmlAccessorType(XmlAccessType.FIELD)
	public class Customer {

	    @XmlAttribute
	    protected int id;

	    @XmlElement
	    protected String fullname;

	    public Customer() {}

	    public int getId() { return this.id; }
	    public void setId(int id) { this.id = id; }

	    public String getFullName() { return this.fullname; }
	    public void setFullName(String name} { this.fullname = name; }
	}

The `@javax.xml.bind.annotation.XmlRootElement` annotation is put on Java classes to denote that they are XML elements. The `name()` attribute of `@XmlRootElement` specifies the string to use for the name of the XML element. In our example, the annotation `@XmlRootElement` specifies that our `Customer` objects should be marshalled into an XML element named `<customer>`.

The `@javax.xml.bind.annotation.XmlAttribute` annotation was placed on the id field of our `Customer` class. This annotation tells JAXB to map the field to an id attribute on the main `<Customer>` element of the XML document. The `@XmlAttribute` annotation also has a `name()` attribute that allows you to specify the exact name of the XML attribute within the XML document. By default, it is the same name as the annotated field.

In our example, the `@javax.xml.bind.annotation.XmlElement` annotation was placed on the fullname field of our `Customer` class. This annotation tells JAXB to map the field to a `<fullname>` element within the main `<Customer>` element of the XML document. `@XmlElement` does have a `name()` attribute, so you can specify the exact string of the XML element. By default, it is the same name as the annotated field.

If we were to output an instance of our `Customer` class that had an `id` of `42` and a name of “Bill Burke,” the outputted XML would look like this:

	<customer id="42">
	   <fullname>Bill Burke</fullname>
	</customer>

You can also use the `@XmlElement` annotation to embed other JAXB-annotated classes. For example, let’s say we wanted to add an `Address` class to our `Customer` class:

	@XmlRootElement(name="address")
	@XmlAccessorType(XmlAccessType.FIELD)
	public class Address  {

	   @XmlElement
	   protected String street;

	   @XmlElement
	   protected String city;

	   @XmlElement
	   protected String state;

	   @XmlElement
	   protected String zip;

	   // getters and setters

	   ...
	}

We would simply add a field to `Customer` that was of type `Address` as follows:

	@XmlRootElement(name="customer")
	@XmlAccessorType(XmlAccessType.FIELD)
	public class Customer {

	    @XmlAttribute
	    protected int id;

	    @XmlElement
	    protected String name;

	    @XmlElement
	    protected Address address;

	    public Customer() {}

	    public int getId() { return this.id; }
	    public void setId(int id) { this.id = id; }
	...
	}

If we were to output an instance of our new `Customer` class that had an `id` of `42`, a name of “Bill Burke,” a street of “200 Marlborough Street,” a city of “Boston,” a state of “MA,” and a zip of “02115,” the outputted XML would look like this:

	<customer id="42">
	   <name>Bill Burke</name>
	   <address>
	      <street>200 Marlborough Street</street>
	      <city>Boston</city>
	      <state>MA</state>
	      <zip>02115</zip>
	   </address>
	</customer>

There are a number of other annotations and settings that allow you to do some more complex Java-to-XML mappings. JAXB implementations are also required to have command-line tools that can automatically generate JAXB-annotated Java classes from XML schema documents. If you need to integrate with an existing XML schema, these autogeneration tools are the way to go.

To marshal Java classes to and from XML, you need to interact with the `javax.xml.bind.JAXBContext` class. `JAXBContext` instances introspect your classes to understand the structure of your annotated classes. They are used as factories for the `javax.xml.bind.Marshaller` and `javax.xml.bind.Unmarshaller` interfaces. `Marshaller` instances are used to take Java objects and output them as XML. `Unmarshaller` instances are used to take XML input and create Java objects out of it. Here’s an example of using JAXB to write an instance of the `Customer` class we defined earlier into XML and then to take that XML and re-create the `Customer` object:

	Customer customer = new Customer();
	customer.setId(42);
	customer.setName("Bill Burke");

	JAXBContext ctx = JAXBContext.newInstance(Customer.class);
	StringWriter writer = new StringWriter();

	ctx.createMarshaller().marshal(customer, writer);

	String custString = writer.toString();

	customer = (Customer)ctx.createUnmarshaller()
	              .unmarshal(new StringReader(custString));

We first create an initialized instance of a `Customer` class. We then initialize a `JAXBContext` to understand how to deal with `Customer` classes. We use a `Marshaller` instance created by the method `JAXBContext.createMarshaller()` to write the `Customer` object into a Java string. Next we use the `Unmarshaller` created by the `JAXBContext.createUnmarshaller()` method to re-create the `Customer` object with the XML string we just created.

Now that we have a general idea of how JAXB works, let’s look at how JAX-RS integrates with it.

### JAXB JAX-RS Handlers

The JAX-RS specification requires implementations to automatically support the marshalling and unmarshalling of classes that are annotated with `@XmlRootElement` or `@XmlType` as well as objects wrapped inside `javax.xml.bind.JAXBElement` instances. Here’s an example that interacts using the `Customer` class defined earlier:

	@Path("/customers")
	public class CustomerResource {

	   @GET
	   @Path("{id}")
	   @Produces("application/xml")
	   public Customer getCustomer(@PathParam("id") int id) {

	      Customer cust = findCustomer(id);
	      return cust;
	   }

	   @POST
	   @Consumes("application/xml")
	   public void createCustomer(Customer cust) {
	      ...
	   }
	}

As you can see, once you’ve applied JAXB annotations to your Java classes, it is very easy to exchange XML documents between your client and web services. The built-in JAXB handlers will handle any JAXB-annotated class for the `application/xml`, `text/xml`, or `application/*+xml` media types. By default, they will also manage the creation and initialization of `JAXBContext` instances. Because the creation of `JAXBContext` instances can be expensive, JAX-RS implementations usually cache them after they are first initialized.

#### Managing your own JAXBContexts with ContextResolvers

If you are already familiar with JAXB, you’ll know that many times you need to configure your `JAXBContext` instances a certain way to get the output you desire. The JAX-RS built-in JAXB provider allows you to plug in your own `JAXBContext` instances. The way it works is that you have to implement a factory-like interface called `javax.ws.rs.ext.ContextResolver` to override the default `JAXBContext` creation:

	public interface ContextResolver<T> {

	   T getContext(Class<?> type);
	}

`ContextResolver`s are pluggable factories that create objects of a specific type, for a certain Java type, and for a specific media type. To plug in your own `JAXBContext`, you will have to implement this interface. Here’s an example of creating a specific JAXBContext for our Customer class:

	@Provider
	@Produces("application/xml")
	public class CustomerResolver
	                      implements ContextResolver<JAXBContext> {
	   private JAXBContext ctx;

	   public CustomerResolver() {
	     this.ctx = ...; // initialize it the way you want
	   }


	   public JAXBContext getContext(Class<?> type) {
	      if (type.equals(Customer.class)) {
	         return ctx;
	      } else {
	         return null;
	      }
	   }
	}

Your resolver class must implement `ContextResolver` with the parameterized type of `JAXBContext`. The class must also be annotated with the `@javax.ws.rs.ext.Provider` annotation to identify it as a JAX-RS component. In our example, the `CustomerResolver` constructor initializes a `JAXBContext` specific to our `Customer` class.

You register your `ContextResolver` using the `javax.ws.rs.core.Application` API discussed in Chapters 3 and 14. The built-in JAXB handler will see if there are any registered `ContextResolver`s that can create `JAXBContext` instances. It will iterate through them, calling the `getContext()` method passing in the Java type it wants a `JAXBContext` created for. If the `getContext()` method returns null, it will go on to the next `ContextResolver` in the list. If the `getContext()` method returns an instance, it will use that `JAXBContext` to handle the request. If there are no `ContextResolver`s found, it will create and manage its own `JAXBContext`. In our example, the `CustomerResolver.getContext()` method checks to see if the type is a `Customer` class. If it is, it returns the `JAXBContext` we initialized in the constructor; otherwise, it returns null.

The `@Produces` annotation on your `CustomerResolver` implementation is optional. It allows you to specialize a `ContextResolver` for a specific media type. You’ll see in the next section that you can use JAXB to output to formats other than XML. This is a way to create `JAXBContext` instances for each individual media type in your system.

### JAXB and JSON

JAXB is flexible enough to support formats other than XML. The Jettison[5] open source project has written a JAXB adapter that can input and output the JSON format. JSON is a text-based format that can be directly interpreted by JavaScript. It is the preferred exchange format for Ajax applications. Although not required by the JAX-RS specification, many JAX-RS implementations use Jettison to support marshalling JAXB annotated classes to and from JSON.

JSON is a much simpler format than XML. Objects are enclosed in curly brackets, “{}”, and contain key/value pairs. Values can be quoted strings, Booleans (true or false), numeric values, or arrays of these simple types. Here’s an example:

	{
	  "id" : 42,
	  "name" : "Bill Burke",
	  "married" : true,
	  "kids" : [ "Molly", "Abby" ]
	}

Key and value pairs are separated by a colon character and delimited by commas. Arrays are enclosed in brackets, “[].” Here, our object has four properties—id, name, married, and kids—with varying values.

#### XML to JSON using BadgerFish

As you can see, JSON is a much simpler format than XML. While XML has elements, attributes, and namespaces, JSON only has name/value pairs. There has been some work in the JSON community to produce a mapping between XML and JSON so that one XML schema can output documents of both formats. The de facto standard, BadgerFish, is a widely used XML-to-JSON mapping and is available in most JAX-RS implementations that have JAXB/JSON support. Let’s go over this mapping:

1.. XML element names become JSON object properties and the text values of these elements are contained within a nested object that has a property named “$.” So, if you had the XML `<customer>Bill Burke</customer>`, it would map to `{ "customer" : { "$" : "Bill Burke" }}`.

2.. XML elements become properties of their base element. Suppose you had the following XML:

	<customer>
	   <first>Bill</first>
	   <last>Burke</last>
	</customer>

The JSON mapping would look like:

	{ "customer" :
	   { "first" : { "$" : "Bill"},
	     "last" : { "$" : "Burke" }
	   }
	}

3.. Multiple nested elements of the same name would become an array value. So, the XML:

	<customer>
	   <phone>978-666-5555</phone>
	   <phone>978-555-2233</phone>
	</customer

would look like the following in JSON:

	{ "customer" :
	   { "phone" : [ { "$", "978-666-5555"}, { "$", "978-555-2233"} ] }
	}

4.. XML attributes become JSON properties prefixed with the @ character. So, if you had the XML:

	<customer id="42">
	   <name>Bill Burke</name>
	</customer>

the JSON mapping would look like the following:

	{ "customer" :
	   { "@id" : 42,
	     "name" : "Bill Burke"
	   }
	}

5.. Active namespaces are contained in an `@xmlns` JSON property of the element. The “$” represents the default namespace. All nested elements and attributes would use the namespace prefix as part of their names. So, if we had the XML:

	<customer xmlns="urn:cust" xmlns:address="urn:address">
	   <name>Bill Burke</name>
	   <address:zip>02115</address:zip>
	</customer>

the JSON mapping would be the following:

	{ "customer" :
	    { "@xmlns" : { "$" : "urn:cust",
	                   "address" : "urn:address" } ,
	      "name" : { "$" : "Bill Burke",
	                 "@xmlns" : { "$" : "urn:cust",
	                              "address" : "urn:address" } },
	      "address:zip" : { "$" : "02115",
	                        "@xmlns" : { "$" : "urn:cust",
	                        "address" : "urn:address" }}
	    }
	}

BadgerFish is kind of unnatural when writing JavaScript, but if you want to unify your formats under XML schema, it’s the way to go.

### JSON and JSON Schema

The thing with using XML schema and the BadgerFish mapping to define your JSON data structures is that it is very weird for JavaScript programmers to consume. If you do not need to support XML clients or if you want to provide a cleaner and simpler JSON representation, there are some options available for you.

It doesn’t make much sense to use XML schema to define JSON data structures. The main reason is that JSON is a richer data format that supports things like maps, lists, and numeric, Boolean, and string data. It is a bit quirky modeling these sorts of simple data structures with XML schema. To solve this problem, the JSON community has come up with JSON schema. Here’s an example of what it looks like when you define a JSON data structure representing our customer example:

	{
	 "description":"A customer",
	 "type":"object",

	 "properties":
	   {"first": {"type": "string"},
	    "last" : {"type" : "string"}
	   }
	}

The description property defines the description for the schema. The type property defines what is being described. Next, you define a list of properties that make up your object. I won’t go into a lot of detail about JSON schema, so you should visit [http://www.json-schema.org](http://www.json-schema.org) to get more information on this subject.

If you do a Google search on Java and JSON, you’ll find a plethora of frameworks that help you marshal and unmarshal between Java and JSON. One particularly good one is the Jackson[6] framework. It has a prewritten JAX-RS content handler that can automatically convert Java beans to and from JSON. It can also generate JSON schema documents from a Java object model.

The way it works by default is that it introspects your Java class, looking for properties, and maps them into JSON. For example, if we had the Java class:

	public class Customer {
	   private int id;
	   private String firstName;
	   private String lastName;

	   public int getId() {
	      return id;
	   }

	   public void setId(int id) {
	      this.id = id;
	   }

	   public String getFirstName() {
	      return firstName;
	   }

	   public void setFirstName(String firstName) {
	      this.firstName = firstName;
	   }

	   public String getLastName() {
	      return lastName;
	   }

	   public void setLastName(String lastName) {
	      this.lastName = lastName;
	   }
	}
and sample data:

	{
	   "id" : 42,
	   "firstName" : "Bill",
	   "lastName" : "Burke"
	}

reading in the data to create a `Customer` object would be as easy as this:

	ObjectMapper mapper = new ObjectMapper();
	Customer cust = mapper.readValue(inputStream, Customer.class);

Writing the data would be as easy as this:

	ObjectMapper mapper = new ObjectMapper();
	mapper.writeValue(outputStream, customer);

The Jackson framework’s JAX-RS integration actually does all this work for you, so all you have to do in your JAX-RS classes is specify the output and input format as `application/json` when writing your JAX-RS methods.

## Custom Marshalling

So far in this chapter, we’ve focused on built-in JAX-RS handlers that can marshal and unmarshal message content. Unfortunately, there are hundreds of data formats available on the Internet, and the built-in JAX-RS handlers are either too low level to be useful or may not match the format you need. Luckily, JAX-RS allows you to write your own handlers and plug them into the JAX-RS runtime.

To illustrate how to write your own handlers, we’re going to pretend that there is no built-in JAX-RS JAXB support and instead write one ourselves using JAX-RS APIs.

### MessageBodyWriter

The first thing we’re going to implement is JAXB-marshalling support. To automatically convert Java objects into XML, we have to create a class that implements the `javax.ws.rs.ext.MessageBodyWriter` interface:

	public interface MessageBodyWriter<T> {

	   boolean isWriteable(Class<?> type, Type genericType,
	                       Annotation annotations[],
	                                    MediaType mediaType);

	   long getSize(T t, Class<?> type, Type genericType,
	                 Annotation annotations[], MediaType mediaType);

	   void writeTo(T t, Class<?> type, Type genericType,
	                Annotation annotations[],
	                MediaType mediaType,
	                MultivaluedMap<String, Object> httpHeaders,
	                OutputStream entityStream)
	                       throws IOException, WebApplicationException;
	}

The `MessageBodyWriter` interface has only three methods. The `isWriteable()` method is called by the JAX-RS runtime to determine if the writer supports marshalling the given type. The `getSize()` method is called by the JAX-RS runtime to determine the `Content-Length` of the output. Finally, the `writeTo()` method does all the heavy lifting and writes the content out to the HTTP response buffer. Let’s implement this interface to support JAXB:

	@Provider
	@Produces("application/xml")
	public class JAXBMarshaller implements MessageBodyWriter {

	   public boolean isWriteable(Class<?> type, Type genericType,
	                     Annotation annotations[], MediaType mediaType) {
	      return type.isAnnotationPresent(XmlRootElement.class);
	   }

We start off the implementation of this class by annotating it with the `@javax.ws.rs.ext.Provider` annotation. This tells JAX-RS that this is a deployable JAX-RS component. We must also annotate it with `@Produces` to tell JAX-RS which media types this `MessageBodyWriter` supports. Here, we’re saying that our `JAXBMarshaller` class supports `application/xml`.

The `isWriteable()` method is a callback method that tells the JAX-RS runtime whether or not the class can handle writing out this type. JAX-RS follows this algorithm to find an appropriate `MessageBodyWriter` to write out a Java object into the HTTP response:

1. First, JAX-RS calculates a list of `MessageBodyWriters` by looking at each writer’s `@Produces` annotation to see if it supports the media type that the JAX-RS resource method wants to output.

2. This list is sorted, with the best match for the desired media type coming first. In other words, if our JAX-RS resource method wants to output application/xml and we have three MessageBodyWriters (one produces `application/*`, one supports anything `*/*`, and the last supports `application/xml`), the one producing `application/xml` will come first.

3. Once this list is calculated, the JAX-RS implementation iterates through the list in order, calling the `MessageBodyWriter.isWriteable()` method. If the invocation returns true, that `MessageBodyWriter` is used to output the data.

The `isWriteable()` method takes four parameters. The first one is a `java.lang.Class` that is the type of the object that is being marshalled. We determine the type by calling the `getClass()` method of the object. In our example, we use this parameter to find out if our object’s class is annotated with the `@XmlRootElement` annotation.

The second parameter is a `java.lang.reflect.Type`. This is generic type information about the object being marshalled. We determine it by introspecting the return type of the JAX-RS resource method. We don’t use this parameter in our `JAXBMarshaller.isWriteable()` implementation. This parameter would be useful, for example, if we wanted to know the type parameter of a `java.util.List` generic type.

The third parameter is an array of `java.lang.annotation.Annotation` objects. These annotations are applied to the JAX-RS resource method we are marshalling the response for. Some `MessageBodyWriters` may be triggered by JAX-RS resource method annotations rather than class annotations. In our `JAXBMarshaller` class, we do not use this parameter in our `isWriteable()` implementation.

The fourth parameter is the media type that our JAX-RS resource method wants to produce.

Let’s examine the rest of our `JAXBMarshaller` implementation:

    public long getSize(Object obj, Class<?> type, Type genericType,
                     Annotation[] annotations, MediaType mediaType)
    {
      return −1;
    }

The `getSize()` method is responsible for determining the `Content-Length` of the response. If you cannot easily determine the length, just return –1. The underlying HTTP layer (i.e., a servlet container) will handle populating the `Content-Length` in this scenario or use the chunked transfer encoding.

The first parameter of `getSize()` is the actual object we are outputting. The rest of the parameters serve the same purpose as the parameters for the `isWriteable()` method.

Finally, let’s look at how we actually write the JAXB object as XML:

    public void writeTo(Object target,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream outputStream) throws IOException
    {
        try {
            JAXBContext ctx = JAXBContext.newInstance(type);
            ctx.createMarshaller().marshal(target, outputStream);
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
    }

The `target`, `type`, `genericType`, `annotations`, and `mediaType` parameters of the `writeTo()` method are the same information passed into the `getSize()` and `isWriteable()` methods. The `httpHeaders` parameter is a `javax.ws.rs.core.MultivaluedMap` that represents the HTTP response headers. You may modify this map and add, remove, or change the value of a specific HTTP header as long as you do this before outputting the response body. The `outputStream` parameter is a `java.io.OutputStream` and is used to stream out the data.

Our implementation simply creates a `JAXBContext` using the type parameter. It then creates a `javax.xml.bind.Marshaller` and converts the Java object to XML.

#### Adding pretty printing

By default, JAXB outputs XML without any whitespace or special formatting. The XML output is all one line of text with no new lines or indentation. We may have human clients looking at this data, so we want to give our JAX-RS resource methods the option to pretty-print the output XML. We will provide this functionality using an `@Pretty` annotation. For example:

	@Path("/customers")
	public class CustomerService {

	   @GET
	   @Path("{id}")
	   @Produces("application/xml")
	   @Pretty
	   public Customer getCustomer(@PathParam("id") int id) {...}
	}

Since the `writeTo()` method of our `MessageBodyWriter` has access to the `getCustomer()` method’s annotations, we can implement this easily. Let’s modify our `JAXBMarshaller` class:

	public void writeTo(Object target,
	                    Class<?> type,
	                    Type genericType,
	                    Annotation[] annotations,
	                    MediaType mediaType,
	                    MultivaluedMap<String, Object> httpHeaders,
	                    OutputStream outputStream) throws IOException
	{
		try {
			JAXBContext ctx = JAXBContext.newInstance(type);
			Marshaller m = ctx.createMarshaller();

			boolean pretty = false;
			for (Annotation ann : annotations) {
			   if (ann.annotationType().equals(Pretty.class)) {
			       pretty = true;
			       break;
			   }
			}
			if (pretty) {
			   marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			}

			m.marshal(target, outputStream);
	      } catch (JAXBException ex) {
	        throw new RuntimeException(ex);
	      }
	}

Here, we iterate over the annotations parameter to see if any of them are the `@Pretty` annotation. If `@Pretty` has been set, we set the `JAXB_FORMATTED_OUTPUT` property on the `Marshaller` so that it will format the XML with line breaks and indentation strings.

#### Pluggable JAXBContexts using ContextResolvers

Earlier in this chapter, we saw how you could plug in your own `JAXBContext` using the `ContextResolver` interface. Let’s look at how we can add this functionality to our `JAXBMarshaller` class.

First, we need a way to locate a `ContextResolver` that can provide a custom `JAXBContext`. We do this through the `javax.ws.rs.ext.Providers` interface:

	public interface Providers {

	   <T> ContextResolver<T> getContextResolver(Class<T> contextType,
	                                             MediaType mediaType);

	   <T> MessageBodyReader<T>
	   getMessageBodyReader(Class<T> type, Type genericType,
	                     Annotation annotations[], MediaType mediaType);


	   <T> MessageBodyWriter<T>
	   getMessageBodyWriter(Class<T> type, Type genericType,
	                     Annotation annotations[], MediaType mediaType);


	   <T extends Throwable> ExceptionMapper<T>
	   getExceptionMapper(Class<T> type);

	}

We use the `Providers.getContextResolver()` method to find a `ContextResolver`. We inject a reference to a `Providers` object using the `@Context` annotation. Let’s modify our `JAXBMarshaller` class to add this new functionality:

	@Context
	protected Providers providers;

	public void writeTo(Object target,
	                       Class<?> type,
	                       Type genericType,
	                       Annotation[] annotations,
	                       MediaType mediaType,
	                       MultivaluedMap<String, Object> httpHeaders,
	                       OutputStream outputStream) throws IOException
	{
	   try {
	      JAXBContext ctx = null;
	      ContextResolver<JAXBContext> resolver =
	           providers.getContextResolver(JAXBContext.class, mediaType);
	      if (resolver != null) {
	        ctx = resolver.getContext(type);
	      }
	      if (ctx == null) {
	          // create one ourselves
	          ctx = JAXBContext.newInstance(type);
	      }
	      ctx.createMarshaller().marshal(target, outputStream);
	   } catch (JAXBException ex) {
	     throw new RuntimeException(ex);
	   }
	}

In our `writeTo()` method, we now use the `Providers` interface to find a `ContextResolver` that can give us a custom `JAXBContext`. If one exists, we call `resolver.getContext()`, passing in the type of the object we want a `JAXBContext` for.

The `ContextResolver` returned by `Providers.getContextResolver()` is actually a proxy that sits in front of a list of `ContextResolvers` that can provide `JAXBContext` instances. When `getContextResolver()` is invoked, the proxy iterates on this list, recalling `getContextResolver()` on each individual resolver in the list. If it returns a `JAXBContext` instance, it returns that to the original caller; otherwise, it tries the next resolver in this list.

### MessageBodyReader

Now that we have written a `MessageBodyWriter` to convert a Java object into XML and output it as the HTTP response body, let’s write an unmarshaller that knows how to convert HTTP XML request bodies back into a Java object. To do this, we need to use the `javax.ws.rs.ext.MessageBodyReader` interface:

	public interface MessageBodyReader<T> {

	   boolean isReadable(Class<?> type, Type genericType,
	                      Annotation annotations[], MediaType mediaType);

	   T readFrom(Class<T> type, Type genericType,
	              Annotation annotations[], MediaType mediaType,
	              MultivaluedMap<String, String> httpHeaders,
	              InputStream entityStream)
	                         throws IOException, WebApplicationException;

	}

The `MessageBodyReader` interface has only two methods. The `isReadable()` method is called by the JAX-RS runtime when it is trying to find a `MessageBodyReader` to unmarshal the message body of an HTTP request. The `readFrom()` method is responsible for creating a Java object from the HTTP request body.

Implementing a `MessageBodyReader` is very similar to writing a `MessageBodyWriter`. Let’s look at how we would implement one:

	@Provider
	@Consumes("application/xml")
	public class JAXBUnmarshaller implements MessageBodyReader {

	   public boolean isReadable(Class<?> type, Type genericType,
	                      Annotation annotations[], MediaType mediaType) {
	      return type.isAnnotationPresent(XmlRootElement.class);
	   }

Our `JAXBUnmarshaller` class is annotated with `@Provider` and `@Consumes`. The latter annotation tells the JAX-RS runtime which media types it can handle. The matching rules for finding a `MessageBodyReader` are the same as the rules for matching `MessageBodyWriter`. The difference is that the `@Consumes` annotation is used instead of the `@Produces` annotation to correlate media types.

Let’s now look at how we read and convert our HTTP message into a Java object:

	Object readFrom(Class<Object>, Type genericType,
	                Annotation annotations[], MediaType mediaType,
	                MultivaluedMap<String, String> httpHeaders,
	                InputStream entityStream)
	                      throws IOException, WebApplicationException {

	    try {
	        JAXBContext ctx = JAXBContext.newInstance(type);
	        return ctx.createUnmarshaller().unmarshal(entityStream);
	    } catch (JAXBException ex) {
	        throw new RuntimeException(ex);
	    }
	}

The `readFrom()` method gives us access to the HTTP headers of the incoming request as well as a `java.io.InputStream` that represents the request message body. Here, we just create a `JAXBContext` based on the Java type we want to create and use a `javax.xml.bind.Unmarshaller` to extract it from the stream.

### Life Cycle and Environment

By default, only one instance of each `MessageBodyReader`, `MessageBodyWriter`, or `ContextResolver` is created per application. If JAX-RS is allocating instances of these components (see Chapter 14), the classes of these components must provide a public constructor for which the JAX-RS runtime can provide all the parameter values. A public constructor may only include parameters annotated with the `@Context` annotation. For example:

	@Provider
	@Consumes("application/json")
	public class MyJsonReader implements MessageBodyReader {

	   public MyJsonReader(@Context Providers providers) {
	      this.providers = providers;
	   }
	}

Whether or not the JAX-RS runtime is allocating the component instance, JAX-RS will perform injection into properly annotated fields and setter methods. Again, you can only inject JAX-RS objects that are found using the `@Context` annotation.

## Wrapping Up

In this chapter, you learned that JAX-RS can automatically convert Java objects to a specific data type format and write it out as an HTTP response. It can also automatically read in HTTP request bodies and create specific Java objects that represent the request. JAX-RS has a number of built-in handlers, but you can also write your own custom marshallers and unmarshallers. Chapter 21 walks you through some sample code that you can use to test-drive many of the concepts and APIs introduced in this chapter.

- [5] For more information, see http://jettison.codehaus.org
- [6] http://jackson.codehaus.org/
