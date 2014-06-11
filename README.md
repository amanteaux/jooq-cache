A cache layer on top of jOOQ
=========================================
This layer enables to easily cache query result from jOOQ  

For example, without jOOQ cache : 
```java
List<Book> booksCached = create
						.selectFrom(BOOK)
						.where(BOOK.PUBLISHED_IN.eq(2011))
						.orderBy(BOOK.TITLE)
						.fetch()
						.map(mapper());
```

with jOOQ cache : 
```java
List<Book> booksCached = fetchCached (
							create
							.selectFrom(BOOK)
							.where(BOOK.PUBLISHED_IN.eq(2011))
							.orderBy(BOOK.TITLE)
						)
						.map(mapper());
```

Any query that returns a result can be cached :
```java
int count = fetchCached (
				create
				.select(count())
			)
			.getValue(0, count())
```

A demo project is available on https://github.com/amanteaux/jooq-cache-demo