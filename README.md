## ⚡ Future Challenges (`com.example.future`)

A set of hands-on coding challenges exploring **`CompletableFuture`** from `java.util.concurrent`. Each challenge demonstrates a distinct async composition pattern used in modern Java applications.

| # | Challenge | Pattern | What It Teaches |
|---|-----------|---------|-----------------|
| 1 | **`Challenge1.java`** | `thenApply()` | Synchronous transformation of a future's result (e.g., `String → FormattedUser`) |
| 2 | **`Challenge2.java`** | `thenCompose()` | Chaining dependent async calls (fetch user ID → fetch email with that ID) |
| 3 | **`Challenge3.java`** | `thenCombine()` | Merging results from two independent futures (role + permissions → profile) |
| 4 | **`Challenge4.java`** | `applyToEither()` | Racing two futures and taking the fastest result (primary vs. secondary DB) |
| 5 | **`Challenge5.java`** | `allOf()` | Waiting for multiple parallel futures and combining all results |
| 6 | **`Challenge6.java`** | `exceptionally()` | Graceful error recovery by supplying a fallback value |
| 7 | **`Challenge7.java`** | `handle()` | Bi-function that processes both the success value **and** the exception in one place |
| 8 | **`Challenge8.java`** | `orTimeout()` / `completeOnTimeout()` | Enforcing deadlines (fail on timeout vs. fallback on timeout) |

### Quick Reference

| Method | Signature | Use Case |
|--------|-----------|----------|
| `thenApply` | `fn: T → U` | Map a value synchronously |
| `thenCompose` | `fn: T → CompletableFuture<U>` | Chain another async call dependent on the result |
| `thenCombine` | `fn: (T, U) → V` | Zip two independent futures together |
| `applyToEither` | `fn: T → V` | Race two futures, transform the winner |
| `allOf` | `CompletableFuture<?>...` | Wait for **all** futures to complete |
| `exceptionally` | `fn: Throwable → T` | Provide a fallback when a future fails |
| `handle` | `fn: (T, Throwable) → U` | Handle success **and** failure in one callback |
| `orTimeout` | `(long, TimeUnit)` | Fail with `TimeoutException` if the future takes too long |
| `completeOnTimeout` | `(T, long, TimeUnit)` | Resolve with a default value if the future takes too long |

## 🔧 Configuration

The application is configured via `application.yml`:

- **Server Port**: 8081
- **Database**: H2 in-memory database
- **JPA**: Hibernate with automatic schema creation (create-drop)
- **SQL Logging**: Enabled for debugging

## 📝 License

This project is for educational purposes.