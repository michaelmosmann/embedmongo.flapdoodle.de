import de.flapdoodle.embedmongo.io.IStreamProcessor;

/**
 * Don't output anything. 
 * 
 * @author Konstantin Petrukhnov
 * @date 19.7.2012
 *
 */
public class NullProcessor implements IStreamProcessor{
	
	@Override
	public void process(String block) {
		// do nothing
		
	}

	@Override
	public void onProcessed() {
		// do nothing
		
	}

}