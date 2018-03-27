package tcc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class TCCDecoder {
	
	private List<TemporalPixel> threads;
	private int cur_size;
	private int width;
	private int height;
	private int num_frames;
	
	public TCCDecoder(InputStream src, int wid, int heig, int frames) {
		threads = new ArrayList<TemporalPixel>();
		cur_size = 0;
		
		width = wid;
		height = heig;
		num_frames = frames;
		
		try {
			transform(src);
		} catch (IOException e) {
			System.out.println("All bytes have been read.");
		}
	}
	
	private void transform(InputStream src) throws IOException {
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				
				int next_value = src.read();
				int thread_length = src.read();
				int f = 0;
				while (f < num_frames) {
					try {
						TemporalPixel cur_thread = threads.get(cur_size);
						cur_thread.addState(new TemporalPixel(next_value, thread_length));
						f = (f + thread_length);
						if (f < num_frames) {
							next_value = src.read();
							thread_length = src.read();
						}
					} catch (IndexOutOfBoundsException e) {
						threads.add(new TemporalPixel(next_value, thread_length));
						f = (f + thread_length);
						if (f < num_frames) {
							next_value = src.read();
							thread_length = src.read();
						}
					}
				}
				cur_size++;
			
			}
		}
		System.out.println("Decode List Build complete!");
		System.out.println("Entries: " + threads.size());
		
	}
	
	public void decode(OutputStream stream) {
		
		for (int i = 0; i < num_frames; i++) {
			for (int j = 0; j < threads.size(); j++) {
				try {
					TemporalPixel cur_pixel = threads.get(j);
					stream.write(cur_pixel.getIntensity());
					cur_pixel.ageByFrame();
				} catch (IOException e) {
					System.out.println("Something went wrong!");
				}
			}
		}
		System.out.println("Decoding Finished!");
		
	}
	
}
