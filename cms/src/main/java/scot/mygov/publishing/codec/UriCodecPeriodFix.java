package scot.mygov.publishing.codec;

import org.hippoecm.repository.api.StringCodecFactory;

import java.text.Normalizer;

public class UriCodecPeriodFix extends StringCodecFactory.UriEncoding {

    /**
     *  This class extends Hippo's built-in encoding functionality to
     *  escape periods/full stops. This is used when creating node/folder names.
     *  Periods are replaced with hyphens.
     *
     *  Hippo's page [1] explains how the built-in encoding works, and their
     *  javadoc [2] states that periods are "removed at end; otherwise not changed."
     *
     *  As such, dots were appearing in URLs when content items use their
     *  folder name as the basis for this (i.e. most content).
     *  We do not wish to have periods in node/folder names at all.
     *
     *  [1] https://documentation.bloomreach.com/14/library/concepts/content-repository/node-name-encoding.html
     *  [2] https://javadoc.onehippo.org/14.0/hippo-repository/org/hippoecm/repository/api/doc-files/encoding.html
     *
     */

    @Override
    public String encode(final String input) {
        String encoded = super.encode(input);

        // replace all periods with hyphens:
        char[] chars = Normalizer.normalize(encoded, Normalizer.Form.NFC).toCharArray();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '.') {
                sb.append("-");
            } else {
                sb.append(chars[i]);
            }
        }
        return sb.toString();
    }
}