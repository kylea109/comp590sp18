package tcc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class TCCEncoder {

	private List<TemporalPixel> threads;
	private int cur_size;
	private int width;
	private int height;
	private int num_frames;
	private int[][][] data_volume;
	
	public TCCEncoder(List<TemporalPixel> predefined) {
		threads = predefined;
	}
	
	public TCCEncoder(InputStream src, int wid, int heig, int frames) {
		threads = new ArrayList<TemporalPixel>();
		cur_size = 0;
		
		width = wid;
		height = heig;
		num_frames = frames;
		data_volume = new int[width][height][num_frames];
		
		try {
			transform(src);
		} catch (IOException e) {
			System.out.println("All bytes have been read.");
		}
	}
	
	
	// Takes the linear byte stream and represents it as a 3D volume.
	private void transform(InputStream src) throws IOException {
		
		for (int f = 0; f < num_frames; f++) {
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int next_byte = src.read();
					data_volume[x][y][f] = next_byte; 
				}
			}
		}
		System.out.println("Data Transform Complete.");
		build();
	
	}
	
	
	 // Builds the 3D volume into a frame of pixel threads
	private void build() {	
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				
				int last_value = -1;
				int frames_locked = 0;
				for (int f = 0; f < num_frames; f++) {
					
					int next_value = data_volume[x][y][f];
					if (last_value == next_value && f == (num_frames - 1))  {
						try {
							TemporalPixel cur_thread = threads.get(cur_size);
							cur_thread.addState(new TemporalPixel(last_value, frames_locked + 1));	
						} catch (IndexOutOfBoundsException e) {
							threads.add(new TemporalPixel(last_value, frames_locked + 1));
						}
					} else if (last_value == next_value) {
						frames_locked++;
					} else if (frames_locked == 0) {
						last_value = next_value;
						frames_locked++;
					} else {
						try {
							TemporalPixel cur_thread = threads.get(cur_size);
							cur_thread.addState(new TemporalPixel(last_value, frames_locked));	
						} catch (IndexOutOfBoundsException e) {
							threads.add(new TemporalPixel(last_value, frames_locked));
						}
						last_value = next_value;
						frames_locked = 1;
						if (f == (num_frames - 1)) {
							TemporalPixel cur_thread = threads.get(cur_size);
							cur_thread.addState(new TemporalPixel(last_value, frames_locked));
						}
					}
					
				}
				cur_size++;
				
			}
		}
		System.out.println("Encode List Build Complete!");
		System.out.println("Entries: " + threads.size());
		
	}
	
	// Write the encoded bits to a file
	public void encode(OutputStream stream) {
		
		for (int i = 0; i < threads.size(); i++) {
			TemporalPixel cur_thread = threads.get(i);
			while (cur_thread != null) {
				try {
					stream.write(cur_thread.getIntensity());
					stream.write(cur_thread.getLifespan());
					cur_thread = cur_thread.getNext();
				} catch (IOException e) {
					System.out.println("Something went wrong!");
				}
			}
		}
		System.out.println("Encoding Finished!");
		
	}
	
	
}
