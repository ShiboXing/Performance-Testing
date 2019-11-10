import org.junit.Test;

import static org.junit.Assert.assertEquals;


import org.junit.After;
import org.junit.Before;
import org.mockito.*;

public class CellTest {
	
	Cell c;
	
	
	@Before
	public void Setup()
	{
		c=new Cell();
	}
	
	@Test
	/*
	 * confirm the cell's text is 'X' if the cell is alive
	 */
	public void testToString1()
	{
		c.setAlive(true);
		assertEquals(c.toString(),"X");
	}
	
	@Test
	/*
	 * confirm the cell's text is '.' if the cell is not alive
	 */
	public void testToString2()
	{
		c.setAlive(false);
		assertEquals(c.toString(),".");
	}
	
	@Test
	/*
	 * the Cell's toString() method fetches the text from that cell
	 * and returns it. This test will verify that the AbstractButton's 
	 * getText() method is called.
	 */
	public void testToString3()
	{
		//create a spy of Cell since Cell class extends JButton which extends
		//AbstractButton
		c=Mockito.spy(new Cell());
		c.setAlive(true);
		c.toString();
		//makes sure that it fetches text from AbstracButton
		Mockito.verify(c,Mockito.atLeast(1)).getText();
		
	}
	
	@After
	public void teardown()
	{
		c=null;
	}
	
	

}
