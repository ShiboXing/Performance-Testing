
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.mockito.*;

public class MainPanelTest {
	
	MainPanel mp;
	//used for checking std_out
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		
	@Before
	public void setUp() throws Exception
	{
		mp=new MainPanel(10);	
	}
	
	@Test 
	/*
	 *  checks the System.out.println() prints out 
	 *  the correct message
	 */
	public void TestRunContinuous1() throws Exception
	{
		//fetch the stdout
	    System.setOut(new PrintStream(outContent));
		//run runContinuous for a second and then stop
		new Thread(() -> {
	        try {
	            Thread.sleep(1000);
	            mp.stop();
	        }
	        catch (Exception e){
	            System.err.println(e);
	        }
	    }).start();
		
		mp.runContinuous();

		//System.err.println("print result: "+outContent);
		//pinned down the behavior that runContinuous starts with Running...
		assertTrue(outContent.toString().startsWith("Running..."));
	}
	
	@Test
	/*
	 * Use verify to confirm that backup() is called RunContinuous
	 * are called 
	 */
	public void TestRunContinuous2()
	{
		
		//create a spy object on this class
		mp=Mockito.spy(new MainPanel(10));
		//run runContinuous for a second and then stop
		new Thread(() -> {
	        try {
	            Thread.sleep(1000);
	            mp.stop();
	        }
	        catch (Exception e){
	            System.err.println(e);
	        }
	    }).start();
				
		mp.runContinuous();
		//verify that at least 2 calls to backup have been made
		Mockito.verify(mp,Mockito.atLeast(2)).backup();
	}
	@Test
	/*
	 * confirms the runContinuous() can be stopped with the stop method
	 */
	public void TestRunContinuous3()
	{
		
		
		//run runContinuous for a second and then stop
		new Thread(() -> {
	        try {
	            Thread.sleep(1000);
	            mp.stop();
	        }
	        catch (Exception e){
	            System.err.println(e);
	        }
	    }).start();
		
		long start= System.currentTimeMillis();
		mp.runContinuous();
		
		//confirms that runContinuous stops within 1200 milliseconds(allowing 200 millisecond overhead)
		assertTrue(System.currentTimeMillis()-start<=1200);
	}
	
	
	
	@Test
	/*
	 * confirm the converted result has the same value
	 * on a normal case
	 */
	public void TestConvertToInt1() throws Exception
	{
		//use Reflection to test the private method
		Class[] args=new Class[1];
		args[0]=int.class;
		Method ctt=MainPanel.class.getDeclaredMethod("convertToInt",args);
		ctt.setAccessible(true);
		int res=(int)ctt.invoke(mp,56);
		assertEquals(res,56);
	}
	
	@Test
	/*
	 * confirm the converted result has the same value
	 * on an edge case
	 */
	public void TestConvertToInt2() throws Exception
	{
		//use Reflection to test the private method
		Class[] args=new Class[1];
		args[0]=int.class;
		Method ctt=MainPanel.class.getDeclaredMethod("convertToInt",args);
		ctt.setAccessible(true);
		int res=(int)ctt.invoke(mp,0);
		assertEquals(res,0);
	}
	
	@Test
	/*
	 * confirm the converted result has the same value
	 * on a corner case (since convertToInt is only used to convert the 
	 * number of neighbors, the number of neighbors couldn't be less than 0,
	 * hence this is a corner case)
	 * 
	 * The pinned-down behavior of convertToInt on a negative number is to 
	 * throw a NumberFormatException.
	 */
	public void TestConvertToInt3() throws Exception
	{
		//use Reflection to test the private method
		Class[] args=new Class[1];
		args[0]=int.class;
		Method ctt=MainPanel.class.getDeclaredMethod("convertToInt",args);
		ctt.setAccessible(true);
		
		//check if the method throws a NumberFormatException
		try{
			ctt.invoke(mp,-10);
		}catch(InvocationTargetException e) {
			
			assertEquals(e.getCause().getClass(),NumberFormatException.class);
			return;
		}
		assertTrue(false);
	}
	
	
	
	@After
	public void tearDown() throws Exception
	{
		mp=null;
		//resetting the System's stdout
		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
	}
}
