package org.acme;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

public class Product {

    public Long id;
    public String name;
    public String description;
    public int price;
    public int quantity;

    public Product() {
        // default constructor.
    }

    public Product(String name, String description, int price, int quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    public Product(Long id, String name, String description, int price, int quantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    public static Multi<Product> findAll(PgPool client) {
        return client.query("SELECT id, name, description, price, quantity FROM products").execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(Product::from);
    }

    public static Multi<Product> findAllByPrice(PgPool client) {
        return client.query("SELECT id, name, description, price, quantity FROM products ORDER BY price ASC").execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(Product::from);
    }

    public static Uni<Product> findById(PgPool client, Long id) {
        return client.preparedQuery("SELECT id, name, description, price, quantity FROM products WHERE id = $1").execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
    }

    public Uni<Long> save(PgPool client) {
        return client.preparedQuery("INSERT INTO products (name, description, price, quantity) VALUES ($1, $2, $3, $4) RETURNING id")
                .execute(Tuple.of(name, description, price, quantity))
                .onItem().transform(pgRowSet -> pgRowSet.iterator().next().getLong("id"));
    }

    public Uni<Boolean> update(PgPool client, Long id) {
        return client.preparedQuery("UPDATE products SET name = $1, description = $2, price = $3, quantity = $4 WHERE id = $5")
                .execute(Tuple.of(name, description, price, quantity, id))
                .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    public static Uni<Boolean> delete(PgPool client, Long id) {
        return client.preparedQuery("DELETE FROM products WHERE id = $1").execute(Tuple.of(id))
                .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    private static Product from(Row row) {
        return new Product(row.getLong("id"), row.getString("name"),
                row.getString("description"), row.getInteger("price"),
                row.getInteger("quantity"));
    }
}
