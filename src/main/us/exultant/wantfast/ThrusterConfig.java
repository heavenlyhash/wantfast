package us.exultant.wantfast;

import java.util.*;

public class ThrusterConfig extends EnumMap<ThrusterConfig.Options,Integer> {
	public ThrusterConfig() {
		super(Options.class);
	}

	public static enum Options {
		PORT,
		IO_WORKER_POOL,
		TASK_WORKER_POOL,
	}
}
