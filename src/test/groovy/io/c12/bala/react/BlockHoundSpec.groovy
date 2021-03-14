package io.c12.bala.react

import reactor.blockhound.BlockHound
import reactor.blockhound.BlockingOperationError
import reactor.core.scheduler.Schedulers
import spock.lang.Specification

import java.util.concurrent.ExecutionException
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit

import static java.lang.Thread.sleep

class BlockHoundSpec extends Specification {

    def setupSpec() {
        BlockHound.install()
    }

    def "check if the Block Hound is working or not"() {
        when: "creating a threaded call"
        FutureTask<?> task = new FutureTask<>({ ->
            sleep(1)
            return ""
        })
        Schedulers.parallel().schedule(task)
        task.get(10, TimeUnit.SECONDS)

        then: "BlockingOperationError is thrown"
        def exception = thrown(ExecutionException)

        and: "verify the Exception is Blocking Operation Error thrown by BlockHound"
        exception.cause.class == BlockingOperationError.class
    }
}
