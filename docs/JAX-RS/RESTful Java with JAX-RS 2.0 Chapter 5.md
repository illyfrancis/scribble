[toc]

# Chapter 5. JAX-RS Injection

A lot of JAX-RS is pulling information from an HTTP request and injecting it into a Java method. You may be interested in a fragment of the incoming URI. You might be interested in a URI query string value. The client might be sending critical HTTP headers or cookie values that your service needs to process the request. JAX-RS lets you grab this information à la carte, as you need it, through a set of injection annotations and APIs.

## The Basics

There are a lot of different things JAX-RS annotations can inject. Here is a list of those provided by the specification:

@javax.ws.rs.PathParam

- This annotation allows you to extract values from URI template parameters.

@javax.ws.rs.MatrixParam

- This annotation allows you to extract values from URI matrix parameters.

@javax.ws.rs.QueryParam

- This annotation allows you to extract values from URI query parameters.

@javax.ws.rs.FormParam

- This annotation allows you to extract values from posted form data.

@javax.ws.rs.HeaderParam

- This annotation allows you to extract values from HTTP request headers.

@javax.ws.rs.CookieParam

- This annotation allows you to extract values from HTTP cookies set by the client.

@javax.ws.rs.core.Context

- This class is the all-purpose injection annotation. It allows you to inject various helper and informational objects that are provided by the JAX-RS API.

Usually, these annotations are used on the parameters of a JAX-RS resource method. When the JAX-RS provider receives an HTTP request, it finds a Java method that will service this request. If the Java method has parameters that are annotated with any of these injection annotations, it will extract information from the HTTP request and pass it as a parameter when it invokes the method.

For per-request resources, you may alternatively use these injection annotations on the fields, setter methods, and even constructor parameters of your JAX-RS resource class. Do not try to use these annotations on fields or setter methods if your component model does not follow per-request instantiation. Singletons process HTTP requests concurrently, so it is not possible to use these annotations on fields or setter methods, as concurrent requests will overrun and conflict with each other.

## @PathParam

We looked at `@javax.ws.rs.PathParam` a little bit in Chapters 3 and 4. `@PathParam` allows you to inject the value of named URI path parameters that were defined in @Path expressions. Let’s revisit the `CustomerResource` example that we defined in Chapter 2 and implemented in Chapter 3:

	@Path("/customers")
	public class CustomerResource {
	   ...

	   @Path("{id}")
	   @GET
	   @Produces("application/xml")
	   public StreamingOutput getCustomer(@
	PathParam("id") int id) {
	     ...
	   }
	}

### More Than One Path Parameter

You can reference more than one URI path parameter in your Java methods. For instance, let’s say we are using first and last name to identify a customer in our `CustomerResource`:

	@Path("/customers")
	public class CustomerResource {
	   ...

	   @Path("{first}-{last}")
	   @GET
	   @Produces("application/xml")
	   public StreamingOutput getCustomer(@PathParam("first") String firstName,
	                                      @PathParam("last") String lastName) {
	     ...
	   }
	}

### Scope of Path Parameters

Sometimes a named URI path parameter will be repeated by different @Path expressions that compose the full URI matching pattern of a resource method. The path parameter could be repeated by the class’s @Path expression or by a subresource locator. In these cases, the @PathParam annotation will always reference the final path parameter. For example:

	@Path("/customers/{id}")
	public class CustomerResource {

	   @Path("/address/{id}")
	   @Produces("text/plain")
	   @GET
	   public String getAddress(@PathParam("id") String addressId) {...}
	}

If our HTTP request was `GET /customers/123/address/456`, the `addressId` parameter in the `getAddress()` method would have the 456 value injected.

### PathSegment and Matrix Parameters

`@PathParam` can not only inject the value of a path parameter, it can also inject instances of `javax.ws.rs.core.PathSegment`. The `PathSegment` class is an abstraction of a specific URI path segment:

	package javax.ws.rs.core;

	public interface PathSegment  {

	   String getPath();
	   MultivaluedMap<String, String> getMatrixParameters();

	}

The `getPath()` method is the string value of the actual URI segment minus any matrix parameters. The more interesting method here is `getMatrixParameters()`. This returns a map of all of the matrix parameters applied to a particular URI segment. In combination with `@PathParam`, you can get access to the matrix parameters applied to your request’s URI. For example:

	@Path("/cars/{make}")
	public class CarResource {

	   @GET
	   @Path("/{model}/{year}")
	   @Produces("image/jpeg")
	   public Jpeg getPicture(@PathParam("make") String make,
	                           @PathParam("model") PathSegment car,
	                             @PathParam("year") String year) {
	      String carColor = car.getMatrixParameters().getFirst("color");
	      ...
	   }

In this example, we have a `CarResource` that allows us to get pictures of cars in our database. The `getPicture()` method returns a JPEG image of cars that match the make, model, and year that we specify. The color of the vehicle is expressed as a matrix parameter of the model. For example:

	GET /cars/mercedes/e55;color=black/2006

Here, our make is mercedes, the model is e55 with a color attribute of black, and the year is 2006. While the make, model, and year information can be injected into our getPicture() method directly, we need to do some processing to obtain information about the color of the vehicle.

Instead of injecting the model information as a Java string, we inject the path parameter as a `PathSegment` into the car parameter. We then use this `PathSegment` instance to obtain the color matrix parameter’s value.

#### Matching with multiple PathSegments

Sometimes a particular path parameter matches to more than one URI segment. In these cases, you can inject a list of PathSegments. For example, let’s say a model in our CarResource could be represented by more than one URI segment. Here’s how the getPicture() method might change:

	@Path("/cars/{make}")
	public class CarResource {

	   @GET
	   @Path("/{model : .+}/year/{year}")
	   @Produces("image/jpeg")
	   public Jpeg getPicture(@PathParam("make") String make,
	                           @PathParam("model") List<PathSegment> car,
	                            @PathParam("year") String year) {
	   }
	}

In this example, if our request was `GET /cars/mercedes/e55/amg/year/2006`, the car parameter would have a list of two `PathSegments` injected into it, one representing the `e55` segment and the other representing the `amg` segment. We could then query and pull in matrix parameters as needed from these segments.

### Programmatic URI Information

All this à la carte injection of path parameter data with the @PathParam annotation is perfect most of the time. Sometimes, though, you need a more general raw API to query and browse information about the incoming request’s URI. The interface javax.ws.rs.core.UriInfo provides such an API:

	public interface UriInfo {
	   public String getPath();
	   public String getPath(boolean decode);
	   public List<PathSegment> getPathSegments();
	   public List<PathSegment> getPathSegments(boolean decode);
	   public MultivaluedMap<String, String> getPathParameters();
	   public MultivaluedMap<String, String> getPathParameters(boolean decode);
	...
	}

The `getPath()` methods allow you to obtain the relative path JAX-RS used to match the incoming request. You can receive the path string decoded or encoded. The `getPathSegments()` methods break up the entire relative path into a series of `PathSegment` objects. Like `getPath()`, you can receive this information encoded or decoded. Finally, `getPathParameters()` returns a map of all the path parameters defined for all matching @Path expressions.

You can obtain an instance of the `UriInfo` interface by using the `@javax.ws.rs.core.Context` injection annotation. Here’s an example:

	@Path("/cars/{make}")
	public class CarResource {

	   @GET
	   @Path("/{model}/{year}")
	   @Produces("image/jpeg")
	   public Jpeg getPicture(@Context UriInfo info) {
	      String make = info.getPathParameters().getFirst("make");
	      PathSegment model = info.getPathSegments().get(1);
	      String color = model.getMatrixParameters().getFirst("color");
	...
	   }
	}

In this example, we inject an instance of `UriInfo` into the `getPicture()` method’s info parameter. We then use this instance to extract information out of the URI.

## @MatrixParam

Instead of injecting and processing PathSegment objects to obtain matrix parameter values, the JAX-RS specification allows you to inject matrix parameter values directly through the `@javax.ws.rs.MatrixParam` annotation. Let’s change our `CarResource` example from the previous section to reflect using this annotation:

	@Path("/{make}")
	public class CarResource {

	   @GET
	   @Path("/{model}/{year}")
	   @Produces("image/jpeg")
	   public Jpeg getPicture(@PathParam("make") String make,
	                          @PathParam("model") String model,
	                          @MatrixParam("color") String color) {
	      ...
	   }

Using the @MatrixParam annotation shrinks our code and provides a bit more readability. The only downside of @MatrixParam is that sometimes you might have a repeating matrix parameter that is applied to many different path segments in the URI. For example, what if color shows up multiple times in our car service example?

	GET /mercedes/e55;color=black/2006/interior;color=tan

Here, the color attribute shows up twice: once with the model and once with the interior. Using `@MatrixParam("color")` in this case would be ambiguous and we would have to go back to processing PathSegments to obtain this matrix parameter.

## todo ...

the rest has to be filled in.

## Common Functionality

Each of these injection annotations has a common set of functionality and attributes. Some can automatically be converted from their string representation within an HTTP request into a specific Java type. You can also define default values for an injection parameter when an item does not exist in the request. Finally, you can work with encoded strings directly, rather than having JAX-RS automatically decode values for you. Let’s look into a few of these.

### Automatic Java Type Conversion

All the injection annotations described in this chapter reference various parts of an HTTP request. These parts are represented as a string of characters within the HTTP request. You are not limited to manipulating strings within your Java code, though. JAX-RS can convert this string data into any Java type that you want, provided that it matches one of the following criteria:

1. It is a primitive type. The int, short, float, double, byte, char, and boolean types all fit into this category.
2. It is a Java class that has a constructor with a single String parameter.
3. It is a Java class that has a static method named valueOf() that takes a single String argument and returns an instance of the class.
4. It is a `java.util.List<T>`, `java.util.Set<T>`, or `java.util.SortedSet<T>`, where `T` is a type that satisfies criteria 2 or 3 or is a String. Examples are `List<Double>`, `Set<String>`, or `SortedSet<Integer>`.

#### Primitive type conversion

We’ve already seen a few examples of automatic string conversion into a primitive type. Let’s review a simple example again:

	@GET
	@Path("{id}")
	public String get(@PathParam("id") int id) {...}

Here, we’re extracting an integer ID from a string-encoded segment of our incoming request URI.

#### Java object conversion

Besides primitives, this string request data can be converted into a Java object before it is injected into your JAX-RS method parameter. This object’s class must have a constructor or a static method named valueOf() that takes a single String parameter.

For instance, let’s go back to the `@HeaderParam` example we used earlier in this chapter. In that example, we used `@HeaderParam` to inject a string that represented the `Referer` header. Since `Referer` is a URL, it would be much more interesting to inject it as an instance of `java.net.URL`:

	import java.net.URL;

	@Path("/myservice")
	public class MyService {

	   @GET
	   @Produces("text/html")
	   public String get(@HeaderParam("Referer") URL referer) {
	     ...
	   }
	}

The JAX-RS provider can convert the Referer string header into a `java.net.URL` because this class has a constructor that takes only one String parameter.

This automatic conversion also works well when only a valueOf() method exists within the Java type we want to convert. For instance, let’s revisit the `@MatrixParam` example we used in this chapter. In that example, we used the `@MatrixParam` annotation to inject the color of our vehicle into a parameter of a JAX-RS method. Instead of representing color as a string, let’s define and use a Java enum class:

	public enum Color {
	   BLACK,
	   BLUE,
	   RED,
	   WHITE,
	   SILVER
	}

You cannot allocate Java enums at runtime, but they do have a built-in `valueOf()` method that the JAX-RS provider can use:

	public class CarResource {

	   @GET
	   @Path("/{model}/{year}")
	   @Produces("image/jpeg")
	   public Jpeg getPicture(@PathParam("make") String make,
	                          @PathParam("model") String model,
	                          @MatrixParam("color") Color color) {
	      ...
	   }

JAX-RS has made our lives a bit easier, as we can now work with more concrete Java objects rather than doing string conversions ourselves.

#### ParamConverters

Sometimes a parameter class cannot use the default mechanisms to convert from string values. Either the class has no String constructor or no `valueOf()` method, or the ones that exist won’t work with your HTTP requests. For this scenario, JAX-RS 2.0 has provided an additional component to help with parameter conversions.

	package javax.ws.rs.ext;

	public interface ParamConverter<T> {
	    public T fromString(String value);
	    public String toString(T value);
	}

As you can see from the code, ParamConverter is a pretty simple interface. The `fromString()` method takes a String and converts it to the desired Java type. The `toString()` method does the opposite. Let’s go back to our Color example. It pretty much requires full uppercase for all Color parameters. Instead, let’s write a `ParamConverter` that allows a `Color` string to be any case.

	public class ColorConverter implements ParamConverter<Color> {

	   public Color fromString(String value) {
	      if (value.equalsIgnoreCase(BLACK.toString())) return BLACK;
	      else if (value.equalsIgnoreCase(BLUE.toString())) return BLUE;
	      else if (value.equalsIgnoreCase(RED.toString())) return RED;
	      else if (value.equalsIgnoreCase(WHITE.toString())) return WHITE;
	      else if (value.equalsIgnoreCase(SILVER.toString())) return SILVER;
	      throw new IllegalArgumentException("Invalid color: " + value);
	   }

	   public String toString(Color value) { return value.toString(); }

	}

We’re still not done yet. We also have to implement the `ParamConverterProvider` interface.

	package javax.ws.rs.ext;
	public interface ParamConverterProvider {
	    public <T> ParamConverter<T> getConverter(Class<T> rawType,
	                                              Type genericType,
	                                              Annotation annotations[]);
	}

This is basically a factory for `ParamConverters` and is the component that must be scanned or registered with your Application deployment class.

	@Provider
	public class ColorConverterProvider {

	   private final ColorConverter converter = new ColorConverter();

	   public <T> ParamConverter<T> getConverter(Class<T> rawType,
	                                              Type genericType,
	                                              Annotation[] annotations) {
	      if (!rawType.equals(Color.class)) return null;

	      return converter;
	   }
	}

In our implementation here, we check to see if the rawType is a Color. If not, return null. If it is, then return an instance of our ColorConverter implementation. The `Annotation[]` parameter for the `getConverter()` method points to whatever parameter annotations are applied to the JAX-RS method parameter you are converting. This allows you to tailor the behavior of your converter based on any additional metadata applied.

#### Collections

All the parameter types described in this chapter may have multiple values for the same named parameter. For instance, let’s revisit the `@QueryParam` example from earlier in this chapter. In that example, we wanted to pull down a set of customers from a customer database. Let’s expand the functionality of this query so that we can order the data sent back by any number of customer attributes:

	GET /customers?orderBy=last&orderBy=first

In this request, the `orderBy` query parameter is repeated twice with different values. We can let our JAX-RS provider represent these two parameters as a `java.util.List` and inject this list with one `@QueryParam` annotation:

	import java.util.List;

	@Path("/customers")
	public class CustomerResource {

	   @GET
	   @Produces("application/xml")
	   public String getCustomers(
	                   @QueryParam("start") int start,
	                   @QueryParam("size") int size,
	                   @QueryParam("orderBy") List<String> orderBy) {
	     ...
	   }
	}

You must define the generic type the List will contain; otherwise, JAX-RS won’t know which objects to fill it with.

#### Conversion failures

If the JAX-RS provider fails to convert a string into the Java type specified, it is considered a client error. If this failure happens during the processing of an injection for an `@MatrixParam`, `@QueryParam`, or `@PathParam`, an error status of `404`, “Not Found,” is sent back to the client. If the failure happens with `@HeaderParam` or `@CookieParam`, an error response code of `400`, “Bad Request,” is sent.

### @DefaultValue

In many types of JAX-RS services, you may have parameters that are optional. When a client does not provide this optional information within the request, JAX-RS will, by default, inject a null value for object types and a zero value for primitive types.

Many times, though, a null or zero value may not work as a default value for your injection. To solve this problem, you can define your own default value for optional parameters by using the `@javax.ws.rs.DefaultValue` annotation.

For instance, let’s look back again at the `@QueryParam` example given earlier in this chapter. In that example, we wanted to pull down a set of customers from a customer database. We used the `start` and `size` query parameters to specify the beginning index and the number of customers desired. While we do want to control the amount of customers sent back as a response, we do not want to require the client to send these query parameters when making a request. We can use `@DefaultValue` to set a base index and dataset size:

	import java.util.List;

	@Path("/customers")
	public class CustomerResource {

	   @GET
	   @Produces("application/xml")
	   public String getCustomers(@DefaultValue("0") @QueryParam("start") int start,
	                           @DefaultValue("10") @QueryParam("size") int size) {
	     ...
	   }
	}

Here, we’ve used `@DefaultValue` to specify a default start index of 0 and a default dataset size of 10. JAX-RS will use the string conversion rules to convert the string value of the `@DefaultValue` annotation into the desired Java type.

### @Encoded

URI template, matrix, query, and form parameters must all be encoded by the HTTP specification. By default, JAX-RS decodes these values before converting them into the desired Java types. Sometimes, though, you may want to work with the raw encoded values. Using the `@javax.ws.rs.Encoded` annotation gives you the desired effect:

	@GET
	@Produces("application/xml")
	public String get(@Encoded @QueryParam("something") String str) {...}

Here, we’ve used the `@Encoded` annotation to specify that we want the encoded value of the something query parameter to be injected into the `str` Java parameter. If you want to work solely with encoded values within your Java method or even your entire class, you can annotate the method or class with `@Encoded` and only encoded values will be used.

## Wrapping Up

In this chapter, we examined how to use JAX-RS injection annotations to insert bits and pieces of an HTTP request à la carte into your JAX-RS resource method parameters. While data is represented as strings within an HTTP request, JAX-RS can automatically convert this data into the Java type you desire, provided that the type follows certain constraints. These features allow you to write compact, easily understandable code and avoid a lot of the boilerplate code you might need if you were using other frameworks like the servlet specification. You can test-drive the code in this chapter by flipping to Chapter 20.

- [4] For more information, see http://www.ietf.org/rfc/rfc2109.txt.