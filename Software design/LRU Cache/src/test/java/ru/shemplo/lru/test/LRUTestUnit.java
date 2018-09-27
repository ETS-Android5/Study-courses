package ru.shemplo.lru.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import ru.shemplo.lru.LRUCache;
import ru.shemplo.lru.SimpleLRUCache;

public class LRUTestUnit {

	private final Random random = new Random ();
	
	private <K, V> LRUCache <K, V> getInstance (int capacity) {
		return new SimpleLRUCache <> (capacity);
	}
	
	@Nested
	@DisplayName ("Test cache on instantiation")
	public class InitializationTestUnit {
	
		@Test
		@DisplayName ("Test cache on instantiation")
		public void testSuccessInit () {
			assertNotNull (getInstance (10));
		}
		
		@Test
		@DisplayName ("Test cache on invalid values of constructor")
		public void testUnsuccessInit () {
			try {
				@SuppressWarnings ("unused")
				LRUCache <Integer, String> cache = getInstance (0);
				cache = getInstance (-1);
				
				fail ("Created instance with non-positive capacity");
			} catch (Exception | AssertionError e) {
				// It's OK (expected behavior)
			}
		}
		
		@Test
		@DisplayName ("Test cache on valid value of capacity")
		public void testCapacity () {
			int capacity = 1 + random.nextInt (1000);
			
			LRUCache <Integer, String> cache = getInstance (capacity);
			assertEquals (cache.getCapacity (), capacity);
		}
		
	}
	
	@Nested
	@DisplayName ("Test cache on insert")
	public class InsertTestUnit {
		
		@Test
		@DisplayName ("Test cache on signle PUT")
		public void testNormalSingleInsert () {
			LRUCache <Integer, String> cache = getInstance (100);
			cache.put (32, "Single insert");
			
			assertEquals (1, cache.getSize ());
		}
		
		@Test
		@DisplayName ("Test cache on PUT null value with normal key")
		public void testNullValueInsert () {
			LRUCache <Integer, String> cache = getInstance (100);
			cache.put (63, null);
			
			assertEquals (1, cache.getSize ());
		}
		
		@Test
		@DisplayName ("Test cache on PUT value with NULL keu")
		public void testNullKeyInsert () {
			LRUCache <Integer, String> cache = getInstance (100);
			try {
				cache.put (null, "Insert with null key");
				
				fail ("Inserted value with null key");
			} catch (Exception | AssertionError e) {
				// It's OK (expected behavior)
			}
		}
		
		@Test
		@DisplayName ("Test cache on insert of several values")
		public void testSeveralInserts () {
			int capacity = 10 + random.nextInt (10000),
				toInsert = 10 + random.nextInt (capacity / 2);
			LRUCache <Integer, String> cache = getInstance (capacity);
			for (int i = 0; i < toInsert; i++) {
				int key = random.nextInt (capacity * 2);
				cache.put (key, "" + key);
			}
			
			assertEquals (toInsert, cache.getSize ());
		}
		
		@Test
		@DisplayName ("Test cache on huge insert")
		public void testOverCapacityInserts () {
			int capacity = 10 + random.nextInt (10000),
				toInsert = capacity + 1 + random.nextInt (capacity);
			LRUCache <Integer, String> cache = getInstance (capacity);
			for (int i = 0; i < toInsert; i++) {
				int key = random.nextInt (toInsert);
				cache.put (key, "" + key);
			}
			
			assertEquals (capacity, cache.getSize ());
		}
		
	}
	
	@Nested
	@DisplayName ("Test cache on real operations")
	public class FunctionalityTestUnit {
		
		@Test
		@DisplayName ("Test GET from empty cache by random key")
		public void testGetFromEpty () {
			LRUCache <Integer, String> cache = getInstance (100);
			assertNull (cache.get (random.nextInt ()));
		}
		
		@Test
		@DisplayName ("Test GET after single PUT")
		public void testInsertAndGet () {
			LRUCache <Integer, String> cache = getInstance (100);
			
			int key = random.nextInt ();
			String value = "" + (key * 2);
			cache.put (key, value);
			
			String answer = cache.get (key);
			assertEquals (value, answer);
		}
		
		@Test
		@DisplayName ("Test GET after over capacity insert")
		public void testBigInsertAndGet () {
			int capacity = 10 + random.nextInt (10000),
				toInsert = capacity + random.nextInt (capacity);
			LRUCache <Integer, String> cache = getInstance (capacity);
			
			int firstKey = -(1 + random.nextInt (1000));
			String firstValue = "" + (firstKey);
			cache.put (firstKey, firstValue);
			
			for (int i = 0; i < toInsert; i++) {
				int key = random.nextInt (toInsert);
				cache.put (key, "" + key);
			}
			
			assertNull (cache.get (firstKey));
		}
		
		@Test
		@DisplayName ("Test GET from \"center\" of cache")
		public void testGetFromMiddle () {
			int capacity = 10 + random.nextInt (10000),
				toInsert = capacity + 1 + random.nextInt (capacity);
			LRUCache <Integer, String> cache = getInstance (capacity);
			
			String middleValue = null;
			int middleKey = -1;
			
			for (int i = 0; i < toInsert; i++) {
				int key = random.nextInt (toInsert);
				if (i == toInsert / 2) {
					key = -key; // to make unique
					
					middleValue = "" + key;
					middleKey = key;
				}
				
				cache.put (key, "" + key);
			}
			
			assertEquals (middleValue, cache.get (middleKey));
			
			for (int i = 0; i < toInsert - 1; i++) {
				int key = random.nextInt (toInsert);
				cache.put (key, "" + key);
			}
			
			assertEquals (middleValue, cache.get (middleKey));
		}
		
	}
	
}
