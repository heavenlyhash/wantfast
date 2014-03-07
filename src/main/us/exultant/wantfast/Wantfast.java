package us.exultant.wantfast;

import java.io.*;
import us.exultant.wantfast.thruster.naive.*;

public class Wantfast {
	public static void main(String... args) throws IOException {
		System.out.println("Hello, world.");
		new SimplestServer().connect().serve();
		System.out.println(new SimplestClient().connect().ping());
	}
}
