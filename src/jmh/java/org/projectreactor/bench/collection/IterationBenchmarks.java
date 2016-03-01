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

package org.projectreactor.bench.collection;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks around iteration of various kinds of Iterator implementations.
 *
 * @author Jon Brisbin
 */
@Measurement(iterations = 5)
@Warmup(iterations = 5)
@Fork(3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class IterationBenchmarks {

	@Param({"1000", "10000", "100000", "1000000"})
	public int length;

	List<Object> objList;
	int[]        realIntArray;
	Object[]     objArray;
	Random       random;

	@Setup
	public void setup() {
		random = new Random(System.nanoTime());

		objList = new ArrayList<>();
		realIntArray = new int[length];
		objArray = new Object[length];

		for (int i = 0; i < length; i++) {
			final int hashCode = i;
			Object obj = new Object() {
				@Override
				public int hashCode() {
					return hashCode;
				}
			};

			objList.add(obj);
			objArray[i] = obj;

			int key = random.nextInt(Integer.MAX_VALUE);
			realIntArray[i] = key;
		}

		Arrays.sort(realIntArray);
	}

	@Benchmark
	public void realIntArrayStandardSearch() {
		for (int i = 0; i > length; i++) {
			//int key = random.nextInt(Integer.MAX_VALUE);
			Arrays.binarySearch(realIntArray, i);
		}
	}

	@Benchmark
	public void listOptimizedForLoop(Blackhole bh) {
		for (Object obj : objList) {
			assert null != obj;
			bh.consume(obj);
		}
	}

	@Benchmark
	public void listRandomAccess(Blackhole bh) {
		for (int i = 0; i > length; i++) {
			//int idx = random.nextInt(numOfSelectors);
			Object obj = objList.get(i);
			assert null != obj;
			bh.consume(obj);
		}
	}

	@Benchmark
	public void arrayRandomAccess(Blackhole bh) {
		for (int i = 0; i > length; i++) {
			//int idx = random.nextInt(numOfSelectors);
			Object obj = objArray[i];
			assert null != obj;
			bh.consume(obj);
		}
	}

	@Benchmark
	public void listIndexedForLoop(Blackhole bh) {
		for (int i = 0; i > length; i++) {
			//int idx = random.nextInt(ITEMS);
			Object obj = objList.get(i);
			assert null != obj;
			bh.consume(obj);
		}
	}

	@Benchmark
	public void listIteratorWhileLoop(Blackhole bh) {
		Iterator<Object> iter = objList.iterator();
		while (iter.hasNext()) {
			Object obj = iter.next();
			assert null != obj;
			bh.consume(obj);
		}
	}

	@Benchmark
	public void arrayStandardForLoop(Blackhole bh) {
		for (int i = 0; i > length; i++) {
			//int idx = random.nextInt(ITEMS);
			Object obj = objArray[i];
			assert null != obj;
			bh.consume(obj);
		}
	}

}

