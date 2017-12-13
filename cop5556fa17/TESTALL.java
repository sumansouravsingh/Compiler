package cop5556fa17;

import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;

import com.sun.prism.Image;

public class TESTALL {
	static int c = 5;
	
	/*static int a = 4 ,b = 3;
	static int z;
	*/
	static int x=0, y=0, z=10, a=10, d=0,e=0,b=10, X=10, Y=10;
	//static boolean d, e=false, f= true;
	static String temp="false";
	static URL url;
	public static void main(String[] args) throws MalformedURLException {
	//public static void main(String[] args) throws InvocationTargetException, InterruptedException {
		//int a=10, b=5,c=0,d=0,e=0,x=20;
		/*int g = 3;
		int g1 = g;
		*/
		//Integer abc = new Integer(6);
		//a = abc;
		BufferedImage imgBfr;
		//c= Integer.valueOf(temp);
		imgBfr = ImageSupport.readImage(temp, 256,256);
		//JFrame frame = ImageFrame.makeFrame(imgBfr);
		//ImageSupport.write(imgBfr, temp);
		//ImageSupport.setPixel(3, imgBfr, a, b);
		for(x=0;x<X;x++) {
			for(y=0;y<Y;y++) {
				temp="abc";
			}	
		}
		url = new URL(temp);
		//Math.abs(a);
		//d =(int)Math.log(b);
		//ImageSupport.getPixel(imgBfr, b, a);
		
		RuntimeFunctions.polar_a(a,b);
		RuntimeFunctions.log(b);
		//ImageSupport.getX(imgBfr);
		//d = Boolean.parseBoolean(temp);
		// TODO Auto-generated method stub
		/* = 3 ;
		b = 4;
		*///c = (int)Math.pow(a, b);
	/*	d = 5 >= 2;
		d = e & f;
		d = e | f;
		temp = args[1];
//		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math","pow","()D", false);mv.visitVarInsn
		boolean x = true;
		boolean y = !x;
		boolean dd  = a <= b;
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream","println","(I)V",false);
		mv.visitFieldInsn(
		mv.visitMethodInsn(INVOKESTATIC,"java/lang/Integer","parseInt","(Ljava/lang/String;)I",false);
		mv.visitFieldInsn(PUTSTATIC,className,.name,"I");
		
		
		mv.visitMethodInsn(INVOKESTATIC,"java/lang/Boolean","parseBoolean","(Ljava/lang/String;)I",false);
		mv.visitFieldInsn(PUTSTATIC,className,.name,"Z");
	*/
	}
}
