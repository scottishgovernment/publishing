package scot.mygov.publishing.codec;

import com.github.slugify.Slugify;
import org.hippoecm.repository.api.StringCodecFactory;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

/**
 * Extends StringCodecFactory.UriEncoding in order to add stopwords and other custom replacements.
 */
public class StopwordsUriEncoding extends StringCodecFactory.UriEncoding {

    private static final String STOPWORDS_STRING =
            "a,able,about,across,after,all,almost,also,am,among,an,and,any,are,as,at,be,because,been," +
            "but,by,can,cannot,could,dear,did,do,does,either,else,ever,every,for,from,get,got," +
            "had,has,have,he,her,hers,him,his,how,however,i,if,in,into,is,it,its," +
            "just,least,let,like,likely,may,me,might,most,must,my,neither," +
            "of,off,often,on,or,other,our,own,rather,said,say,says,she,should,since,so,some," +
            "than,that,the,their,them,then,there,these,they,this,tis,to,too,twas,us," +
            "wants,was,we,were,what,when,where,which,while,who,whom,why,will,with,would,yet,you,your";

    private static final Set<String> STOPWORDS = new HashSet<>();

    private final Slugify slugify = new Slugify().withCustomReplacement(".", "-");

    static {
        STOPWORDS.addAll(asList(STOPWORDS_STRING.split(",")));
    }

    @Override
    public String encode(final String input) {
        String slugified = slugify.slugify(input);
        String [] words = slugified.split("-");
        return stream(words).filter(this::notStopword).collect(joining("-"));
    }

    boolean notStopword(String word) {
        return !STOPWORDS.contains(word);
    }
}
