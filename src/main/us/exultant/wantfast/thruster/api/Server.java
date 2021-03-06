package us.exultant.wantfast.thruster.api;

import java.io.*;
import us.exultant.wantfast.*;

public interface Server {
	public void configure(ThrusterConfig cfg);

	public void connect() throws IOException;
}
