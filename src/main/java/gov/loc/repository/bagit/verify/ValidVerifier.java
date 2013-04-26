
package gov.loc.repository.bagit.verify;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.utilities.SimpleResult;

public interface ValidVerifier extends Verifier {

    @Override
    SimpleResult verify(final Bag bag);
}
