package org.acme;

import io.quarkus.runtime.StartupEvent;
import io.vertx.mutiny.pgclient.PgPool;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class DBInit {

    @Inject
    private final PgPool client;
    private final boolean schemaCreate;

    public DBInit(PgPool client, @ConfigProperty(name = "myapp.schema.create", defaultValue = "true") boolean schemaCreate) {
        this.client = client;
        this.schemaCreate = schemaCreate;
    }

    void onStart(@Observes StartupEvent ev) {
        if (schemaCreate) {
            initdb();
        }
    }

    private void initdb() {
        client.query("DROP TABLE IF EXISTS products").execute()
                .flatMap(r -> client.query("CREATE TABLE products (id SERIAL PRIMARY KEY, name TEXT NOT NULL, description TEXT NOT NULL, price int NOT NULL, quantity int NOT NULL)").execute())
                .flatMap(r -> client.query("INSERT INTO products (name, description, price, quantity) VALUES ('Fridge', 'Home products', 8500, 1)").execute())
                .flatMap(r -> client.query("INSERT INTO products (name, description, price, quantity) VALUES ('Chair', 'Home products', 4500, 1)").execute())
                .flatMap(r -> client.query("INSERT INTO products (name, description, price, quantity) VALUES ('Table', 'Home products', 5500, 1)").execute())
                .flatMap(r -> client.query("INSERT INTO products (name, description, price, quantity) VALUES ('Washing Machine', 'Home products', 7500, 1)").execute())
                .await().indefinitely();
    }
}
