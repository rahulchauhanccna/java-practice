package com.example;

import java.util.Comparator;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Simple order book using a NavigableMap (TreeMap) of bid prices -> aggregated volume.
 * Demonstrates merge(), lastKey(), and subMap() for range queries.
 */
public class OrderBook {
    /** NavigableMap storing Price (Key) -> Aggregated Volume (Value), sorted by price. */
    private final NavigableMap<Double, Integer> bids = new TreeMap<>(Comparator.reverseOrder());

    /**
     * Adds an order bid at a specified price.
     * If the price point already exists, add newVolume to the existing volume.
     * If missing, create a new entry with newVolume.
     * Uses Map.merge() for atomic "insert or add" logic.
     */
    public void addBid(double price, int newVolume) {
       bids.merge(price, newVolume, Integer::sum);
    }

    /**
     * Returns the highest bid price currently active in the order book.
     * Runs in O(log n) time via NavigableMap.lastKey().
     * Returns 0.0 if the book is empty.
     */
    public double getHighestBidPrice() {
        return bids.isEmpty() ? 0.0 : bids.firstKey();
    }

    /**
     * Returns a view of all bids falling within [minPrice, maxPrice] inclusive.
     * Modifying this returned view directly reflects in the underlying map.
     * Uses NavigableMap.subMap() for an efficient range view.
     */
    public NavigableMap<Double, Integer> getBidsInRange(double minPrice, double maxPrice) {
        return bids.subMap(minPrice, true, maxPrice, true);
    }

    public static void main(String[] args) {
        OrderBook book = new OrderBook();

        // Populate sample bids
        book.addBid(150.50, 1000);
        book.addBid(150.50, 200);   // Volume at 150.50 should now be 1200
        book.addBid(149.80, 3000);
        book.addBid(152.10, 500);
        book.addBid(148.00, 1500);

        // Verification checks
        System.out.println("Highest Bid: " + book.getHighestBidPrice()); // Expected: 152.10

        System.out.println("Bids between 149.00 and 151.00:");
        NavigableMap<Double, Integer> rangeView = book.getBidsInRange(149.00, 151.00);
        rangeView.forEach((price, vol) -> System.out.println("  $" + price + " -> " + vol + " shares"));
        // Expected:
        //   $149.8 -> 3000 shares
        //   $150.5 -> 1200 shares
    }
}