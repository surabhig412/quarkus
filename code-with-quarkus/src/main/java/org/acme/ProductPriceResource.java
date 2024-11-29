package org.acme;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.net.URI;


@Path("/productsByPrice")
public class ProductPriceResource {

    @Inject
    private final PgPool client;

    public ProductPriceResource(PgPool client) {
        this.client = client;
    }

    @GET
    public Multi<Product> getByPrice() {
        return Product.findAllByPrice(client);
    }

}
