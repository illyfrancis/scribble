[toc]

# Chapter 7. Server Responses and Exception Handling

So far, the examples given in this book have been very clean and tidy. The JAX-RS resource methods we have written have looked like regular vanilla Java methods with JAX-RS annotations. We haven’t talked a lot about the default behavior of JAX-RS resource methods, particularly around HTTP response codes in success and failure scenarios. Also, in the real world, you can’t always have things so neat and clean. Many times you need to send specific response headers to deal with complex error conditions. This chapter first discusses the default response codes that vanilla JAX-RS resource methods give. It then walks you through writing complex responses using JAX-RS APIs. Finally, it goes over how exceptions can be handled within JAX-RS.

## Default Response Codes

The default response codes that JAX-RS uses are pretty straightforward. There is pretty much a one-to-one relationship to the behavior described in the HTTP 1.1 Method Definition specification.[7].] Let’s examine what the response codes would be for both success and error conditions for the following JAX-RS resource class:

	@Path("/customers")
	public class CustomerResource {

	   @Path("{id}")
	   @GET
	   @Produces("application/xml")
	   public Customer getCustomer(@PathParam("id") int id) {...}

	   @POST
	   @Produces("application/xml")
	   @Consumes("application/xml")
	   public Customer create(Customer newCust) {...}

	   @PUT
	   @Path("{id}")
	   @Consumes("application/xml")
	   public void update(@PathParam("id") int id, Customer cust) {...}

	   @Path("{id}")
	   @DELETE
	   public void delete(@PathParam("id") int id) {...}
	}

### Successful Responses

Successful HTTP response code numbers range from 200 to 399. For the `create()` and `getCustomer()` methods of our `CustomerResource` class, they will return a response code of 200, “OK,” if the Customer object they are returning is not null. If the return value is null, a successful response code of 204, “No Content,” is returned. The 204 response is not an error condition. It just tells the client that everything went OK, but that there is no message body to look for in the response. If the JAX-RS resource method’s return type is void, a response code of 204, “No Content,” is returned. This is the case with our update() and delete() methods.

The HTTP specification is pretty consistent for the PUT, POST, GET, and DELETE methods. If a successful HTTP response contains a message body, 200, “OK,” is the response code. If the response doesn’t contain a message body, 204, “No Content,” must be returned.

### Error Responses

In our `CustomerResource` example, error responses are mostly driven by application code throwing an exception. We will discuss this exception handling later in this chapter. There are some default error conditions that we can talk about right now, though.

Standard HTTP error response code numbers range from 400 to 599. In our example, if a client mistypes the request URI, for example, to customers, it will result in the server not finding a JAX-RS resource method that can service the request. In this case, a 404, “Not Found,” response code will be sent back to the client.

For our `getCustomer()` and `create()` methods, if the client requests a `text/html` response, the JAX-RS implementation will automatically return a 406, “Not Acceptable,” response code with no response body. This means that JAX-RS has a relative URI path that matches the request, but doesn’t have a JAX-RS resource method that can produce the client’s desired response media type. (Chapter 9 talks in detail about how clients can request certain formats from the server.)

If the client invokes an HTTP method on a valid URI to which no JAX-RS resource method is bound, the JAX-RS runtime will send an error code of 405, “Method Not Allowed.” So, in our example, if our client does a PUT, GET, or DELETE on the `/customers` URI, it will get a 405 response because POST is the only supported method for that URI. The JAX-RS implementation will also return an `Allow` response header back to the client that contains a list of HTTP methods the URI supports. So, if our client did a `GET /customers` in our example, the server would send this response back:

	HTTP/1.1 405, Method Not Allowed
	Allow: POST

The exception to this rule is the HTTP HEAD and OPTIONS methods. If a JAX-RS resource method isn’t available that can service HEAD requests for that particular URI, but there does exist a method that can handle GET, JAX-RS will invoke the JAX-RS resource method that handles GET and return the response from that minus the request body. If there is no existing method that can handle OPTIONS, the JAX-RS implementation is required to send back some meaningful, automatically generated response along with the `Allow` header set.

## Complex Responses

Sometimes the web service you are writing can’t be implemented using the default request/response behavior inherent in JAX-RS. For the cases in which you need to explicitly control the response sent back to the client, your JAX-RS resource methods can return instances of `javax.ws.rs.core.Response`:

	public abstract class Response {

	   public abstract Object getEntity();
	   public abstract int getStatus();
	   public abstract MultivaluedMap<String, Object> getMetadata();
	...
	}

The Response class is an abstract class that contains three simple methods. The getEntity() method returns the Java object you want converted into an HTTP message body. The getStatus() method returns the HTTP response code. The getMetadata() method is a MultivaluedMap of response headers.

Response objects cannot be created directly; instead, they are created from `javax.ws.rs.core.Response.ResponseBuilder` instances returned by one of the static helper methods of Response:

	public abstract class Response {
	...
	   public static ResponseBuilder status(Status status) {...}
	   public static ResponseBuilder status(int status) {...}
	   public static ResponseBuilder ok() {...}
	   public static ResponseBuilder ok(Object entity) {...}
	   public static ResponseBuilder ok(Object entity, MediaType type) {...}
	   public static ResponseBuilder ok(Object entity, String type) {...}
	   public static ResponseBuilder ok(Object entity, Variant var) {...}
	   public static ResponseBuilder serverError() {...}
	   public static ResponseBuilder created(URI location) {...}
	   public static ResponseBuilder noContent() {...}
	   public static ResponseBuilder notModified() {...}
	   public static ResponseBuilder notModified(EntityTag tag) {...}
	   public static ResponseBuilder notModified(String tag) {...}
	   public static ResponseBuilder seeOther(URI location) {...}
	   public static ResponseBuilder temporaryRedirect(URI location) {...}
	   public static ResponseBuilder notAcceptable(List<Variant> variants) {...}
	   public static ResponseBuilder fromResponse(Response response) {...}
	...
	}

If you want an explanation of each and every static helper method, the JAX-RS Javadocs are a great place to look. They generally center on the most common use cases for creating custom responses. For example:

	public static ResponseBuilder ok(Object entity, MediaType type) {...}

The `ok()` method here takes the Java object you want converted into an HTTP response and the `Content-Type` of that response. It returns a preinitialized `ResponseBuilder` with a status code of 200, “OK.” The other helper methods work in a similar way, setting appropriate response codes and sometimes setting up response headers automatically.

The `ResponseBuilder` class is a factory that is used to create one individual Response instance. You store up state you want to use to create your response and when you’re finished, you have the builder instantiate the Response:

	public static abstract class ResponseBuilder {

	   public abstract Response build();
	   public abstract ResponseBuilder clone();

	   public abstract ResponseBuilder status(int status);
	   public ResponseBuilder status(Status status) {...}

	   public abstract ResponseBuilder entity(Object entity);
	   public abstract ResponseBuilder type(MediaType type);
	   public abstract ResponseBuilder type(String type);

	   public abstract ResponseBuilder variant(Variant variant);
	   public abstract ResponseBuilder variants(List<Variant> variants);

	   public abstract ResponseBuilder language(String language);
	   public abstract ResponseBuilder language(Locale language);

	   public abstract ResponseBuilder location(URI location);
	   public abstract ResponseBuilder contentLocation(URI location);

	   public abstract ResponseBuilder tag(EntityTag tag);
	   public abstract ResponseBuilder tag(String tag);

	   public abstract ResponseBuilder lastModified(Date lastModified);
	   public abstract ResponseBuilder cacheControl(CacheControl cacheControl);

	   public abstract ResponseBuilder expires(Date expires);
	   public abstract ResponseBuilder header(String name, Object value);

	   public abstract ResponseBuilder cookie(NewCookie... cookies);
	}

As you can see, ResponseBuilder has a lot of helper methods for initializing various response headers. I don’t want to bore you with all the details, so check out the JAX-RS Javadocs for an explanation of each one. I’ll be giving examples using many of them throughout the rest of this book.

Now that we have a rough idea about creating custom responses, let’s look at an example of a JAX-RS resource method setting some specific response headers:

	@Path("/textbook")
	public class TextBookService {

	   @GET
	   @Path("/restfuljava")
	   @Produces("text/plain")
	   public Response getBook() {

	       String book = ...;
	       ResponseBuilder builder = Response.ok(book);
	       builder.language("fr")
	               .header("Some-Header", "some value");

	       return builder.build();
	   }
	}

Here, our getBook() method is returning a plain-text string that represents a book our client is interested in. We initialize the response body using the `Response.ok()` method. The status code of the ResponseBuilder is automatically initialized with 200. Using the `ResponseBuilder.language()` method, we then set the `Content-Language` header to French. We then use the `ResponseBuilder.header()` method to set a custom response header. Finally, we create and return the Response object using the `ResponseBuilder.build()` method.

One interesting thing to note about this code is that we never set the `Content-Type` of the response. Because we have already specified an `@Produces` annotation, the JAX-RS runtime will set the media type of the response for us.

### Returning Cookies

JAX-RS also provides a simple class to represent new cookie values. This class is `javax.ws.rs.core.NewCookie`:

	public class NewCookie extends Cookie {

		public static final int DEFAULT_MAX_AGE = −1;

		public NewCookie(String name, String value) {}

		public NewCookie(String name, String value, String path,
		                  String domain, String comment,
		                    int maxAge, boolean secure) {}

		public NewCookie(String name, String value, String path,
		                  String domain, int version, String comment,
		                   int maxAge, boolean secure) {}

		public NewCookie(Cookie cookie) {}

		public NewCookie(Cookie cookie, String comment,
		                  int maxAge, boolean secure) {}

		public static NewCookie valueOf(String value)
		                  throws IllegalArgumentException {}

		public String getComment() {}
		public int getMaxAge() {}
		public boolean isSecure() {}
		public Cookie toCookie() {}
	}

The NewCookie class extends the Cookie class discussed in Chapter 5. To set response cookies, create instances of `NewCookie` and pass them to the method `ResponseBuilder.cookie()`. For example:

	@Path("/myservice")
	public class MyService {

		@GET
		public Response get() {

		    NewCookie cookie = new NewCookie("key", "value");
		    ResponseBuilder builder = Response.ok("hello", "text/plain");
		    return builder.cookie(cookie).build();
	}

Here, we’re just setting a cookie named key to the value value.

### The Status Enum

Generally, developers like to have constant variables represent raw strings or numeric values within. For instance, instead of using a numeric constant to set a Response status code, you may want a static final variable to represent a specific code. The JAX-RS specification provides a Java enum called `javax.ws.rs.core.Response.Status` for this very purpose:

	public enum Status {
	   OK(200, "OK"),
	   CREATED(201, "Created"),
	   ACCEPTED(202, "Accepted"),
	   NO_CONTENT(204, "No Content"),
	   MOVED_PERMANENTLY(301, "Moved Permanently"),
	   SEE_OTHER(303, "See Other"),
	   NOT_MODIFIED(304, "Not Modified"),
	   TEMPORARY_REDIRECT(307, "Temporary Redirect"),
	   BAD_REQUEST(400, "Bad Request"),
	   UNAUTHORIZED(401, "Unauthorized"),
	   FORBIDDEN(403, "Forbidden"),
	   NOT_FOUND(404, "Not Found"),
	   NOT_ACCEPTABLE(406, "Not Acceptable"),
	   CONFLICT(409, "Conflict"),
	   GONE(410, "Gone"),
	   PRECONDITION_FAILED(412, "Precondition Failed"),
	   UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
	   INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
	   SERVICE_UNAVAILABLE(503, "Service Unavailable");

	   public enum Family {
	         INFORMATIONAL, SUCCESSFUL, REDIRECTION,
	         CLIENT_ERROR, SERVER_ERROR, OTHER
	   }

	   public Family getFamily()

	   public int getStatusCode()

	   public static Status fromStatusCode(final int statusCode)
	}

Each Status enum value is associated with a specific family of HTTP response codes. These families are identified by the `Status.Family` Java enum. Codes in the 100 range are considered informational. Codes in the 200 range are considered successful. Codes in the 300 range are success codes, but fall under the redirection category. Error codes are in the 400 to 500 ranges. The 400s are client errors and 500s are server errors.

Both the `Response.status()` and `ResponseBuilder.status()` methods can accept a Status enum value. For example:

	@DELETE
	Response delete() {
	   ...

	   return Response.status(Status.GONE).build();
	}

Here, we’re telling the client that the thing we want to delete is already gone (410).

### javax.ws.rs.core.GenericEntity

When we’re dealing with returning `Response` objects, we do have a problem with `MessageBodyWriters` that are sensitive to generic types. For example, what if our built-in JAXB `MessageBodyWriter` can handle lists of JAXB objects? The `isWriteable()` method of our JAXB handler needs to extract parameterized type information of the generic type of the response entity. Unfortunately, there is no easy way in Java to obtain generic type information at runtime. To solve this problem, JAX-RS provides a helper class called `javax.ws.rs.core.GenericEntity`. This is best explained with an example:

	@GET
	@Produces("application/xml")
	public Response getCustomerList() {
	    List<Customer> list = new ArrayList<Customer>();
	    list.add(new Customer(...));

	    GenericEntity entity = new GenericEntity<List<Customer>>(list){};
	    return Response.ok(entity).build();
	}

The `GenericEntity` class is a Java generic template. What you do here is create an anonymous class that extends `GenericEntity`, initializing the `GenericEntity`’s template with the generic type you’re using. If this looks a bit magical, it is. The creators of Java generics made things a bit difficult, so we’re stuck with this solution.

## Exception Handling

Errors can be reported to a client either by creating and returning the appropriate Response object or by throwing an exception. Application code is allowed to throw any checked (classes extending java.lang.Exception) or unchecked (classes extending java.lang.RuntimeException) exceptions they want. Thrown exceptions are handled by the JAX-RS runtime if you have registered an exception mapper. Exception mappers can convert an exception to an HTTP response. If the thrown exception is not handled by a mapper, it is propagated and handled by the container (i.e., servlet) JAX-RS is running within. JAX-RS also provides the `javax.ws.rs.WebApplicationException`. This can be thrown by application code and automatically processed by JAX-RS without having to write an explicit mapper. Let’s look at how to use the `WebApplicationException` first. We’ll then examine how to write your own specific exception mappers.

### javax.ws.rs.WebApplicationException

JAX-RS has a built-in unchecked exception that applications can throw. This exception is preinitialized with either a `Response` or a particular status code:

	public class WebApplicationException extends RuntimeException {

	   public WebApplicationException() {...}
	   public WebApplicationException(Response response) {...}
	   public WebApplicationException(int status) {...}
	   public WebApplicationException(Response.Status status) {...}
	   public WebApplicationException(Throwable cause) {...}
	   public WebApplicationException(Throwable cause,
	                                       Response response) {...}
	   public WebApplicationException(Throwable cause, int status) {...}
	   public WebApplicationException(Throwable cause,
	                                   Response.Status status) {...}

	   public Response getResponse() {...]
	}

When JAX-RS sees that a `WebApplicationException` has been thrown by application code, it catches the exception and calls its `getResponse()` method to obtain a `Response` to send back to the client. If the application has initialized the `WebApplicationException` with a status code or Response object, that code or Response will be used to create the actual HTTP response. Otherwise, the WebApplicationException will return a status code of 500, “Internal Server Error,” to the client.

For example, let’s say we have a web service that allows clients to query for customers represented in XML:

	@Path("/customers")
	public class CustomerResource {

	   @GET
	   @Path("{id}")
	   @Produces("application/xml")
	   public Customer getCustomer(@PathParam("id") int id) {

	       Customer cust = findCustomer(id);
	       if (cust == null) {
	         throw new WebApplicationException(Response.Status.NOT_FOUND);
	       }
	       return cust;
	   }
	}

In this example, if we do not find a Customer instance with the given ID, we throw a `WebApplicationException` that causes a 404, “Not Found,” status code to be sent back to the client.

### Exception Mapping

Many applications have to deal with a multitude of exceptions thrown from application code and third-party frameworks. Relying on the underlying servlet container to handle the exception doesn’t give us much flexibility. Catching and then wrapping all these exceptions within WebApplicationException would become quite tedious. Alternatively, you can implement and register instances of `javax.ws.rs.ext.ExceptionMapper`. These objects know how to map a thrown application exception to a Response object:

	public interface ExceptionMapper<E extends Throwable> {
	{
	   Response toResponse(E exception);
	}

For example, one exception that is commonly thrown in Java Persistence API (JPA)–based database applications is `javax.persistence.EntityNotFoundException`. It is thrown when JPA cannot find a particular object in the database. Instead of writing code to handle this exception explicitly, you could write an `ExceptionMapper` to handle this exception for you. Let’s do that:

	@Provider
	public class EntityNotFoundMapper
	     implements ExceptionMapper<EntityNotFoundException> {

	   public Response toResponse(EntityNotFoundException e) {
	      return Response.status(Response.Status.NOT_FOUND).build();
	   }
	}

Our `ExceptionMapper` implementation must be annotated with the `@Provider` annotation. This tells the JAX-RS runtime that it is a component. The class implementing the `ExceptionMapper` interface must provide the parameterized type of the `ExceptionMapper`. JAX-RS uses this generic type information to match up thrown exceptions to ExceptionMappers. Finally, the `toResponse()` method receives the thrown exception and creates a Response object that will be used to build the HTTP response.

JAX-RS supports exception inheritance as well. When an exception is thrown, JAX-RS will first try to find an ExceptionMapper for that exception’s type. If it cannot find one, it will look for a mapper that can handle the exception’s superclass. It will continue this process until there are no more superclasses to match against.

Finally, ExceptionMappers are registered with the JAX-RS runtime using the deployment APIs discussed in Chapter 14.

### Exception Hierarchy

JAX-RS 2.0 has added a nice exception hierarchy for various HTTP error conditions. So, instead of creating an instance of WebApplicationException and initializing it with a specific status code, you can use one of these exceptions instead. We can change our previous example to use `javax.ws.rs.NotFoundException`:

	@Path("/customers")
	public class CustomerResource {

	   @GET
	   @Path("{id}")
	   @Produces("application/xml")
	   public Customer getCustomer(@PathParam("id") int id) {

	       Customer cust = findCustomer(id);
	       if (cust == null) {
	         throw new NotFoundException());
	       }
	       return cust;
	   }
	}

Like the other exceptions in the exception hierarchy, `NotFoundException` inherits from `WebApplicationException`. If you looked at the code, you’d see that in its constructor it is initializing the status code to be 404. Table 7-1 lists some other exceptions you can use for error conditions that are under the `javax.ws.rs package`.

	                      Table 7-1. JAX-RS exception hierarchy

	Exception						Status code Description
	------------------------------- ----------- ------------------------------------------
	BadRequestException				400			Malformed message
	NotAuthorizedException			401			Authentication failure
	ForbiddenException				403			Not permitted to access
	NotFoundException 				404 		Couldn’t find resource
	NotAllowedException 			405 		HTTP method not supported
	NotAcceptableException 			406 		Client media type requested not supported
	NotSupportedException 			415 		Client posted media type not supported
	InternalServerErrorException 	500 		General server error 
	ServiceUnavailableException 	503 		Server is temporarily unavailable or busy

`BadRequestException` is used when the client sends something to the server that the server cannot interpret. The JAX-RS runtime will actually throw this exception in certain scenarios. The most obvious is when a PUT or POST request has submitted malformed XML or JSON that the `MessageBodyReader` fails to parse. JAX-RS will also throw this exception if it fails to convert a header or cookie value to the desired type. For example:

	@HeaderParam("Custom-Header") int header;
	@CookieParam("myCookie") int cookie;

If the HTTP request’s `Custom-Header` value or the `myCookie` value cannot be parsed into an integer, `BadRequestException` is thrown.

`NotAuthorizedException` is used when you want to write your own authentication protocols. The 401 HTTP response code this exception represents requires you to send back a challenge header called `WWW-Authenticate`. This header is used to tell the client how it should authenticate with the server. `NotAuthorizedException` has a few convenience constructors that make it easier to build this header automatically:

    public NotAuthorizedException(Object challenge, Object... moreChallenges) {}

For example, if I wanted to tell the client that OAuth Bearer tokens are required for authentication, I would throw this exception:

	throw new NotAuthorizedException("Bearer");

The client would receive this HTTP response:

	HTTP/1.1 401 Not Authorized
	WWW-Authenticate: Bearer

`ForbiddenException` is generally used when the client making the invocation does not have permission to access the resource it is invoking on. In Java EE land, this is usually because the authenticated client does not have the specific role mapping required.

`NotFoundException` is used when you want to tell the client that the resource it is requesting does not exist. There are also some error conditions where the JAX-RS runtime will throw this exception automatically. If the JAX-RS runtime fails to inject into an @PathParam, @QueryParam, or @MatrixParam, it will throw this exception. Like in the conditions discussed for `BadRequestException`, this can happen if you are trying to convert to a type the parameter value isn’t meant for.

`NotAllowedException` is used when the HTTP method the client is trying to invoke isn’t supported by the resource the client is accessing. The JAX-RS runtime will automatically throw this exception if there isn’t a JAX-RS method that matches the invoked HTTP method.

`NotAcceptableException` is used when the client is requesting a specific format through the Accept header. The JAX-RS runtime will automatically throw this exception if there is not a JAX-RS method with an @Produces annotation that is compatible with the client’s Accept header.

`NotSupportedException` is used when a client is posting a representation that the server does not understand. The JAX-RS runtime will automatically throw this exception if there is no JAX-RS method with an `@Consumes` annotation that matches the Content-Type of the posted entity.

`InternalServerErrorException` is a general-purpose error that is thrown by the server. For applications, you would throw this exception if you’ve reached an error condition that doesn’t really fit with the other HTTP error codes. The JAX-RS runtime throws this exception if a `MessageBodyWriter` fails or if there is an exception thrown from an `ExceptionMapper`.

`ServiceUnavailableException` is used when the server is temporarily unavailable or busy. In most cases, it is OK for the client to retry the request at a later time. The HTTP 503 status code is often sent with a `Retry-After` header. This header is a suggestion to the client when it might be OK to retry the request. 

Its value is in seconds or a formatted date string. `ServiceUnavailableException` has a few convenience constructors to help with initializing this header:

    public ServiceUnavailableException(Long retryAfter) {}
    public ServiceUnavailableException(Date retryAfter) {}

#### Mapping default exceptions

What’s interesting about the default error handling for JAX-RS is that you can write an `ExceptionMapper` for these scenarios. For example, if you want to send back a different response to the client when JAX-RS cannot find an `@Produces` match for an Accept header, you can write an ExceptionMapper for `NotAcceptableException`. This gives you complete control on how errors are handled by your application.

## Wrapping Up

In this chapter, you learned that JAX-RS has default response codes for both success and error conditions. For more complex responses, your JAX-RS resource methods can return `javax.ws.rs.core.Response` objects. JAX-RS has a few exception utilities. You can throw instances of `javax.ws.rs.WebApplicationException` or let the underlying servlet container handle the exception. Or, you can write an `ExceptionMapper` that can map a particular exception to an HTTP response. Chapter 22 walks you through some sample code that you can use to test-drive many of the concepts and APIs introduced in this chapter.

- [7] For more information, see http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html