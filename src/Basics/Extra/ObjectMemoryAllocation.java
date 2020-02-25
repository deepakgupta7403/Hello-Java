package Basics.Extra;

/**
 *In Java, all objects are dynamically allocated on Heap. This is different from C++ where objects can be allocated memory either on Stack or on Heap.
 * In C++, when we allocate the object using new(), the object is allocated on Heap, otherwise on Stack if not global or static.
 *
 * In Java, when we only declare a variable of a class type, only a reference is created (memory is not allocated for the object). To allocate memory to an object, we must use new().
 * So the object is always allocated memory on heap (See this for more details).
 *
 * If we create a class variable then it'll not allocate the memory in memory Heap.
 * When we use new keyWord then then it'll allocate the memory in memory Heap.
 */


public class ObjectMemoryAllocation {

    public static void main(String[] args)
    {
        Test t;

        // Error here because t
        // is not initialzed
//       t.show();    //TODO UNCOMMENT FOR TEST

        //Error
        //java: variable t might not have been initialized



        // all objects are dynamically
        // allocated
        Test t2 = new Test();
        t2.show(); // No error

        //OUTPUT
        //Test::show() called
    }
}

class Test {

    // class contents
    void show()
    {
        System.out.println("Test::show() called");
    }
}
