/*
 * Copyright (c) 2011-2014 Pivotal Software, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.projectreactor.bench.reactor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.core.publisher.TopicProcessor;
import reactor.core.publisher.WorkQueueProcessor;

import static reactor.bus.selector.Selectors.$;

/**
 * @author Jon Brisbin
 */
@Measurement(iterations = 5, time = 5)
@Warmup(iterations = 5)
@Fork(3)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class EventBusBenchmarks {

	@Param({"100", "1000", "10000"})
	public int    numOfSelectors;
	@Param({"sync", "ringBuffer", "workQueue", "threadPoolExecutor"})
	public String dispatcher;

	private CountDownLatch latch;
	private EventBus        reactor;
	private Object[]       keys;
	private Event<?>       event;

	@Setup
	public void setup() {
		switch(dispatcher){
			case "ringBuffer":
				reactor = EventBus.create(TopicProcessor.create());
				break;
			case "workQueue":
				reactor = EventBus.create(WorkQueueProcessor.create(), 4);
				break;
			default:
				reactor = EventBus.create();
		}
		event = new Event<>(null);
		latch = new CountDownLatch(numOfSelectors);
		keys = new Object[numOfSelectors];
		for (int i = 0; i < numOfSelectors; i++) {
			keys[i] = new Object();
			reactor.on($(keys[i]), event -> latch.countDown());
		}
	}

	@TearDown
	public void tearDown() {
		reactor.getProcessor().onComplete();
	}

	@Benchmark
	public void reactorThroughput(Blackhole bh) throws InterruptedException {
		for (int i = 0; i < numOfSelectors; i++) {
			reactor.notify(keys[i], event);
			bh.consume(i);
		}

		assert latch.await(30, TimeUnit.SECONDS);
	}

}
