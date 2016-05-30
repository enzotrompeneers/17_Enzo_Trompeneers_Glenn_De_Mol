package hellotvxlet;

public class Singleton 
{
       private static Singleton instance = new Singleton(); // create obj
       private Singleton(){} // private constructor
       public static Singleton getInstance() // only obj available
       {
          return instance;
       }
       public void MessageUp() 
       {
           System.out.println("up");
       }
       public void MessageDown() 
       {
           System.out.println("down");
       }
       public void MessageLeft() 
       {
           System.out.println("left");
       }
       public void MessageRight() 
       {
           System.out.println("right");
       }
       public void MessageWin() 
       {
           System.out.println("puzzle solved!");
       }
}
