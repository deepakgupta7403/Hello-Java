package Basics.Extra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * There are two in-built methods to sort in Java.
 * 1) Arrays.Sort() works for arrays which can be of primitive data type also.
 * 2) Collections.sort() works for objects Collections like ArrayList and LinkedList
 **/

public class Sorting {
    public static void main(String[] args) {

        Sorting sorting = new Sorting();
        sorting.arraySort();
        sorting.collectionSort();
        sorting.reverseArraySort();
        sorting.reverseCollectionSort();
        sorting.subArraySort();
    }

    //1)Array.Sort()
    private void arraySort() {
        int[] arr = {13, 7, 6, 45, 21, 9, 101, 102};

        Arrays.sort(arr);
        System.out.printf("Modified arr[] : %s", Arrays.toString(arr));

        //OUTPUT
        //Modified arr[] : [6, 7, 9, 13, 21, 45, 101, 102]
    }

    //2)Collections.sort()
    private void collectionSort() {
        // Create a list of strings
        ArrayList<String> al = new ArrayList<String>();
        al.add("Geeks For Geeks");
        al.add("Friends");
        al.add("Dear");
        al.add("Is");
        al.add("Superb");

        /* Collections.sort method is sorting the
        elements of ArrayList in ascending order. */
        Collections.sort(al);

        // Let us print the sorted list
        System.out.println("List after the use of" + " Collection.sort() :\n" + al);

        //OUTPUT
        //List after the use of Collection.sort() :
        //[Dear, Friends, Geeks For Geeks, Is, Superb]
    }

    //Which sorting algorithm does Java use in sort()?
    /**Previously, Java’s Arrays.sort method used Quicksort for arrays of primitives and Merge sort for arrays of objects.
     * In the latest versions of Java, Arrays.sort method and Collection.sort() uses Timsort.**/

    //Which order of sorting is done by default?
    /**It by default sorts in ascending order.**/

    //How to sort array or list in descending order?
    /**It can be done with the help of Collections.reverseOrder().**/


    //1)Reverse List using Array.Sort()
    private void reverseArraySort() {
        // Note that we have Integer here instead of
        // int[] as Collections.reverseOrder doesn't
        // work for primitive types.
        Integer[] arr = {13, 7, 6, 45, 21, 9, 2, 100};

        // Sorts arr[] in descending order
        Arrays.sort(arr, Collections.reverseOrder());

        System.out.printf("Modified arr[] : %s", Arrays.toString(arr));

        //OUTPUT
        //Modified arr[] : [100, 45, 21, 13, 9, 7, 6, 2]
    }

    //2)Reverse List using Collection.sort()
    private void reverseCollectionSort() {
        // Create a list of strings
        ArrayList<String> al = new ArrayList<String>();
        al.add("Geeks For Geeks");
        al.add("Friends");
        al.add("Dear");
        al.add("Is");
        al.add("Superb");

        /* Collections.sort method is sorting the
        elements of ArrayList in ascending order. */
        Collections.sort(al, Collections.reverseOrder());

        // Let us print the sorted list
        System.out.println("List after the use of" + " Collection.sort() :\n" + al);


        //OUTPUT
        //List after the use of Collection.sort() :
        //[Superb, Is, Geeks For Geeks, Friends, Dear]
    }

    //3) Sort subArry(Specific Pos to Pos)
    private void subArraySort() {
        // Our arr contains 8 elements
        int[] arr = { 13, 7, 6, 45, 21, 9, 2, 100 };

        // Sort subarray from index 1 to 4, i.e.,
        // only sort subarray {7, 6, 45, 21} and
        // keep other elements as it is.
        Arrays.sort(arr, 1, 5);

        System.out.printf("Modified arr[] : %s", Arrays.toString(arr));

        //OUTPUT
        //Modified arr[] : [13, 6, 7, 21, 45, 9, 2, 100]
    }

}
