/**
 * 
 */
package org.verapdf.pdfa.qa;

import org.apache.commons.codec.digest.DigestUtils;
import org.verapdf.pdfa.flavours.PDFAFlavour;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *
 */
public class ZipBackedTestCorpus extends AbstractTestCorpus<ZipEntry> {
    private final static String PDF_SUFFIX = ".pdf";
    private final ZipFile zipSource;

    private ZipBackedTestCorpus(final CorpusDetails details,
            final File zipSource, final PDFAFlavour flavour) throws ZipException, IOException {
        super(details, itemsMapFromZipSource(zipSource, flavour));
        this.zipSource = new ZipFile(zipSource);
    }

    /**
     * { @inheritDoc }
     * 
     * @throws IOException
     */
    @Override
    protected InputStream getStreamFromReference(ZipEntry reference)
            throws IOException {
        return this.zipSource.getInputStream(reference);
    }

    /**
     * @param name
     *            a String name for the TestCorpus instance
     * @param description
     *            a textual description of the TestCorpus instance
     * @param zipFile
     *            a {@link File} instance that's a zip file for the corpus
     * @return a TestCorpus instance initialised from the passed params and zip
     *         file
     * @throws IOException
     *             if there's an exception parsing the zip file
     * @throws ZipException
     *             if there's an exception parsing the zip file
     */
    public static TestCorpus fromZipSource(final String name,
            final String description, final File zipFile, final PDFAFlavour flavour) throws ZipException,
            IOException {
        if (name == null)
            throw new NullPointerException("Parameter name can not be null");
        if (name.isEmpty())
            throw new NullPointerException("Parameter name can not be empty");
        if (description == null)
            throw new NullPointerException(
                    "Parameter description can not be null");
        String hexSha1 = "";
        try (InputStream is = new FileInputStream(zipFile)) {
            hexSha1 = DigestUtils.sha1Hex(is);
        }

        return new ZipBackedTestCorpus(CorpusDetailsImpl.fromValues(name, description, hexSha1),
                zipFile, flavour);
    }

    private static final Map<String, ZipEntry> itemsMapFromZipSource(
            final File zipFile, final PDFAFlavour flavour) throws ZipException, IOException {
        Map<String, ZipEntry> itemMap = new HashMap<>();
        try (ZipFile zipSource = new ZipFile(zipFile)) {
            Enumeration<? extends ZipEntry> entries = zipSource.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()
                        || !entry.getName().endsWith(PDF_SUFFIX))
                    continue;
                if (flavour == null) {
                    itemMap.put(entry.getName(), entry);
                } else {
                    if (matchFlavour(entry.getName(), flavour)) {
                        itemMap.put(entry.getName(), entry);
                    }
                }
            }
        }
        return itemMap;
    }

    private static boolean matchFlavour(final String item, final PDFAFlavour flavour) {
        return item.contains(flavour.toString());
    }

}
