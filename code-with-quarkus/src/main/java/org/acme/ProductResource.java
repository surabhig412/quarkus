package org.acme;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import io.vertx.mutiny.pgclient.PgPool;
import jakarta.ws.rs.core.Response;

import java.net.URI;


@Path("/products")
public class ProductResource {

    @Inject
    private final PgPool client;

    public ProductResource(PgPool client) {
        this.client = client;
    }

    @GET
    public Multi<Product> get() {
        return Product.findAll(client);
    }

    @GET
    @Path("{id}")
    public Uni<Response> getSingle(Long id) {
        return Product.findById(client, id)
                .onItem().transform(product -> product != null ? Response.ok(product) : Response.status(Response.Status.NOT_FOUND))
                .onItem().transform(Response.ResponseBuilder::build);
    }

    @POST
    public Uni<Response> create(Product product) {
        return product.save(client)
                .onItem().transform(id -> URI.create("/products/" + id))
                .onItem().transform(uri -> Response.created(uri).build());
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(Product product, Long id) {
        return product.update(client, id)
                .onItem().transform(updated -> updated ? Response.Status.OK : Response.Status.NOT_FOUND)
                .onItem().transform(status -> Response.status(status).build());
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(Long id) {
        return Product.delete(client, id)
                .onItem().transform(deleted -> deleted ? Response.Status.NO_CONTENT : Response.Status.NOT_FOUND)
                .onItem().transform(status -> Response.status(status).build());
    }

//    @POST
//    @Path("{id}/stock")
//    public Uni<Response> getStockAvailability(Long id) {
//        return Product.findById(client, id)
//                .onItem().transform(product -> product != null ? Response.ok(product) : Response.status(Response.Status.NOT_FOUND))
//                .onItem().transform(Response.ResponseBuilder::build);
//    }
}
