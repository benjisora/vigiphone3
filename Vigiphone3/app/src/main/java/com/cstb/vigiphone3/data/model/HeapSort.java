package com.cstb.vigiphone3.data.model;

import java.util.List;

/**
 * HeapSort class, used to sort the values
 */
public class HeapSort {

    private static int N;

    /**
     * Sorts the given array
     *
     * @param arr The array to sort
     */
    public static void sort(List<Emitter> arr) {
        heapify(arr);
        for (int i = N; i > 0; i--) {
            swap(arr, 0, i);
            N = N - 1;
            maxheap(arr, 0);
        }
    }

    /**
     * Builds a heap
     *
     * @param arr The array to sort
     */
    public static void heapify(List<Emitter> arr) {
        N = arr.size() - 1;
        for (int i = N / 2; i >= 0; i--)
            maxheap(arr, i);
    }

    /**
     * Swaps the largest element in the heap
     *
     * @param arr The array to sort
     * @param i   The array element
     */
    public static void maxheap(List<Emitter> arr, int i) {
        int left = 2 * i;
        int right = 2 * i + 1;
        int max = i;
        if (left <= N && arr.get(left).getPassloss() > arr.get(i).getPassloss())
            max = left;
        if (right <= N && arr.get(right).getPassloss() > arr.get(max).getPassloss())
            max = right;

        if (max != i) {
            swap(arr, i, max);
            maxheap(arr, max);
        }
    }

    /**
     * Swaps two elements into the array
     *
     * @param arr The array to sort
     * @param i   The first element
     * @param j   The second element
     */
    public static void swap(List<Emitter> arr, int i, int j) {
        Emitter tmp = arr.get(i);
        arr.set(i, arr.get(j));
        arr.set(j, tmp);
    }

}
