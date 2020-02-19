package Extra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * There are two ways to do binary search in Java.
 * 1) Arrays.binarysearch() works for arrays which can be of primitive data type also.
 * 2) Collections.binarysearch() works for objects Collections like ArrayList and LinkedList.
 **/

public class BinarySearch {

    public static void main(String[] args) {
        ArraysBinarySearch arraysBinarySearch = new ArraysBinarySearch();
        CollectionBinarySearch collectionBinarySearch = new CollectionBinarySearch();

    }


    static class ArraysBinarySearch {
        public void main(String[] args) {
            int arr[] = {10, 20, 15, 22, 35};
            Arrays.sort(arr);

            int key = 22;
            int res = Arrays.binarySearch(arr, key);
            if (res >= 0)
                System.out.println(key + " found at index = "
                        + res);
            else
                System.out.println(key + " Not found");

            key = 40;
            res = Arrays.binarySearch(arr, key);
            if (res >= 0)
                System.out.println(key + " found at index = "
                        + res);
            else
                System.out.println(key + " Not found");
        }

        //OUTPUT
        //22 found at index = 3
        //40 Not found
    }

    static class CollectionBinarySearch {
        public void main(String[] args) {
            List<Integer> al = new ArrayList<Integer>();
            al.add(1);
            al.add(2);
            al.add(3);
            al.add(10);
            al.add(20);

            // 10 is present at index 3.
            int key = 10;
            int res = Collections.binarySearch(al, key);
            if (res >= 0)
                System.out.println(key + " found at index = "
                        + res);
            else
                System.out.println(key + " Not found");

            key = 15;
            res = Collections.binarySearch(al, key);
            if (res >= 0)
                System.out.println(key + " found at index = "
                        + res);
            else
                System.out.println(key + " Not found");
        }

        //OUTPUT
        //10 found at index = 3
        //15 Not found
    }



    //Implement Own Binary Search iN java;
    // Java implementation of recursive Binary Search
    public class OwnBinarySearch
    {
        // Returns index of x if it is present in arr[l..
        // r], else return -1
        int binarySearch(int arr[], int l, int r, int x)
        {
            if (r>=l)
            {
                int mid = l + (r - l)/2;

                // If the element is present at the
                // middle itself
                if (arr[mid] == x)
                    return mid;

                // If element is smaller than mid, then
                // it can only be present in left subarray
                if (arr[mid] > x)
                    return binarySearch(arr, l, mid-1, x);

                // Else the element can only be present
                // in right subarray
                return binarySearch(arr, mid+1, r, x);
            }

            // We reach here when element is not present
            //  in array
            return -1;
        }

        // Driver method to test above
        public void main(String args[])
        {
            OwnBinarySearch ob = new OwnBinarySearch();
            int arr[] = {2,3,4,10,40};
            int n = arr.length;
            int x = 10;
            int result = ob.binarySearch(arr,0,n-1,x);
            if (result == -1)
                System.out.println("Element not present");
            else
                System.out.println("Element found at index " +
                        result);
        }


        //OUTPUT
        //Element found at index 3
    }
}
