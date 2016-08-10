/**
 * 
 */
package org.verapdf.pdfa.qa;

import org.verapdf.pdfa.flavours.PDFAFlavour;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *
 */
@XmlRootElement(namespace = "http://www.verapdf.org/corpus", name = "corpus")
abstract class AbstractTestCorpus<L> implements TestCorpus {
    @XmlElement(name = "details")
    private final CorpusDetails details;
    @XmlElementWrapper
    @XmlElement(name = "item")
    private final Map<String, L> itemMap;

    protected AbstractTestCorpus(final CorpusDetails details,
            final Map<String, L> itemMap) {
        this.details = details;
        this.itemMap = new HashMap<>(itemMap);
    }

    /**
     * { @inheritDoc }
     */
    @Override
    public CorpusDetails getDetails() {
        return this.details;
    }

    /**
     * { @inheritDoc }
     */
    @Override
    public int getItemCount() {
        return this.itemMap.size();
    }

    /**
     * { @inheritDoc }
     */
    @Override
    public Set<String> getItemNames() {
        return Collections.unmodifiableSet(this.itemMap.keySet());
    }

    /**
     * { @inheritDoc }
     */
    @Override
    public Set<String> getItemNamesForFlavour(PDFAFlavour flavour) {
        // TODO Look at implementing filtering by flavour
        return Collections.unmodifiableSet(this.itemMap.keySet());
    }

    /**
     * { @inheritDoc }
     */
    @Override
    public InputStream getItemStream(String itemName) throws IOException {
        if (!this.itemMap.containsKey(itemName))
            throw new IOException("No element found for name=" + itemName);
        return getStreamFromReference(this.itemMap.get(itemName));
    }

    abstract protected InputStream getStreamFromReference(final L reference)
            throws IOException;

    /**
     * { @inheritDoc }
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.details == null) ? 0 : this.details.hashCode());
        result = prime
                * result
                + ((this.getItemNames() == null) ? 0 : this.getItemNames()
                        .hashCode());
        return result;
    }

    /**
     * { @inheritDoc }
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof TestCorpus))
            return false;
        TestCorpus other = (TestCorpus) obj;
        if (this.details == null) {
            if (other.getDetails() != null)
                return false;
        } else if (!this.details.equals(other.getDetails()))
            return false;
        if (this.getItemNames() == null) {
            if (other.getItemNames() != null)
                return false;
        } else if (!this.getItemNames().equals(other.getItemNames()))
            return false;
        return true;
    }

}
