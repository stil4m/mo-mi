# mo-mi: Mongo Migrations in Java


A simple library to perform migration scripts on your MongoDB database. 

## Why do I need this?

In MongoDB you have unstructured data and no schema. So why do you need migrations? Your data is unstructed which allows you to easily change the format in which you store it in your database. Somethimes you want to update the existing data in your database to comply to a new structure. Herefor you would use mo-mi. With these migrations you can easily update the data in your production environment.

## How does it work?
You just write Javascript file containing the changes you want to perform in a single changeset. The content of these javascript files are sent to the MongoDB Java driver with the `doEval()` method. This way you could write the same commando's you would write in the normal MongoDB shell *.

When you have defined your migration and it is executed, you are not allowed to change it. This is done to make sure that your database data is consistent when you deploy different versions of your application to multiple servers (it's for your own safety).

## How do I use it?

### Setting up the migrator

```
MongoClient client = new MongoClient();
DB db = client.getDB("my_database");
MigrationLoader migrationLoader = new ClassPathMigrationLoader();
Migrator migrator = new Migrator(db, migrationLoader);
migrator.migrate();
```

The example uses the `ClassPathMigrationLoader`, but you could also provide your own `MigrationLoader`.

When you call the `migrate()` method, the migrations are performed. Each migration is stored in a collection of your database. The default collection is `MoMiMigrations` which is also configurable in the constructor of the `Migrator`.
  

### Defining the migrations

The default implementation of `ClassPathMigrationLoader` expects that a `migrations.txt` file is located at the root of the configured context path which is a simple text file containing a comma seperated list of files. An example would be:

```
foo.js,
bar.js, baz.js,
other.js
```

The file paths must be relative paths to the `ClassPathMigrationLoader` context path. The default context path is `/`, but you can configure this in the constructor of the migration laoder.

## Issues and question

Please create issues if you require new features of find bugs. These will be resolved as fast as possilbe or create your own pull request.

For questions please email me on the email adress listed in my GitHub account details.

