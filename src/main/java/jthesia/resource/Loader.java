package jthesia.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class Loader {
	
	private static final ExecutorService executor = Executors.newSingleThreadExecutor(); 
	
	public static final class LoadException extends RuntimeException {
		private static final long serialVersionUID = -8841888210608384019L;
		public LoadException(Exception e) {
			super(e);
		}
	}
	
	public interface LoadedReciever<T> {
		public void resourceLoaded(T resource);
	}
	
	public static <T> void loadAssync(Supplier<T> load, LoadedReciever<T> reciever){
		
		try {
			CompletableFuture.supplyAsync(load, executor).thenAcceptAsync(loaded -> {
				if(reciever != null) {
					reciever.resourceLoaded(loaded);
				}
			});
		}
		catch (Exception e) {
			throw new LoadException(e);
		}
		
	}
	
	public static <T> void loadAssync(Supplier<T> load){
		loadAssync(load, null);
	}

	public static void destroy() {
		executor.shutdown();
	}

}
