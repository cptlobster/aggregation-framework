# Deploying Aggregation Framework

You can either:
- Use the Runner in the `aggregation-framework-runner` package.
- Integrate Aggregation Framework into your existing package.

## Using the Runner

If you have a consumer of class `MyConsumer`, this code should get you started:

```scala
import dev.cptlobster.aggregation_framework.{Runner, Collector}
import MyConsumer

class MyRunner extends Runner {
  override val collectors: List[Collector] = List(MyConsumer())
}
```

Then you should be able to run `MyRunner`. The subcommands are as follows:
- `oneshot`: Run all one-shot consumers (that do not have scheduling rules)
- `daemon`: Start the daemon to run scheduled consumers based on their scheduling rules
- `list`: List all defined runners

Run `aggregation-framework --help` for a full command-line argument reference.

### Scheduling

Depending on how frequently you are running your consumers, you should do so in one of two ways:
- If you are running jobs frequently, it is recommended that you use the `ScheduledRunner` implementation and run the
  `daemon` subcommand. Using cronjobs is not recommended in this case due to JVM startup times.
- If you are running consumer(s) infrequently and want to save resources (at the cost of slower startup), use a 
  system-level job scheduler (such as a cronjob or a systemd timer) and run your consumer(s) as oneshot. For example:
  ```cronexp
  0 2 * * * aggregation-framework oneshot -n DailyConsumer
  ```
  would run the consumer named `DailyConsumer` at 2AM every day.

## Integrating

`Consumer` implements the `Runnable` interface, which should make it compatible with Java concurrency abstractions. Once
you implement your consumer, you can execute it in any Java/Scala program by calling the `Consumer.run()` function, or
by passing the Consumer to a `Thread`, `ThreadPool`, or any other standard Java concurrency / executor class.