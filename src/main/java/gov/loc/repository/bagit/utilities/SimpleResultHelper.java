
package gov.loc.repository.bagit.utilities;

import gov.loc.repository.bagit.verify.CompleteVerifier;
import gov.loc.repository.bagit.verify.ManifestVerifier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimpleResultHelper {

    public static void missingPayloadFile(final SimpleResult result,
            final String manifest, final String filepath) {
        result.setSuccess(false);
        result.addMessage(
                CompleteVerifier.CODE_PAYLOAD_MANIFEST_CONTAINS_MISSING_FILE,
                "Payload manifest {0} contains missing file(s): {1}", manifest,
                filepath);
    }

    public static void missingTagFile(final SimpleResult result,
            final String manifest, final String filepath) {
        result.setSuccess(false);
        result.addMessage(
                CompleteVerifier.CODE_TAG_MANIFEST_CONTAINS_MISSING_FILE,
                "Tag manifest {0} contains missing file(s): {1}", manifest,
                filepath);
    }

    public static void invalidPayloadFile(final SimpleResult result,
            final String manifest, final String filepath) {
        result.setSuccess(false);
        result.addMessage(
                ManifestVerifier.CODE_PAYLOAD_MANIFEST_CONTAINS_INVALID_FILE,
                "Payload manifest {0} contains invalid file(s): {1}", manifest,
                filepath);
    }

    public static void invalidTagFile(final SimpleResult result,
            final String manifest, final String filepath) {
        result.setSuccess(false);
        result.addMessage(
                ManifestVerifier.CODE_TAG_MANIFEST_CONTAINS_INVALID_FILE,
                "Tag manifest {0} contains invalid files: {1}", manifest,
                filepath);
    }

    public static boolean isMissingOrInvalid(final SimpleResult result,
            final String filepath) {
        if (containsObject(result,
                CompleteVerifier.CODE_TAG_MANIFEST_CONTAINS_MISSING_FILE,
                filepath)) {
            return true;
        }
        if (containsObject(result,
                CompleteVerifier.CODE_PAYLOAD_MANIFEST_CONTAINS_MISSING_FILE,
                filepath)) {
            return true;
        }
        if (containsObject(result,
                ManifestVerifier.CODE_TAG_MANIFEST_CONTAINS_INVALID_FILE,
                filepath)) {
            return true;
        }
        if (containsObject(result,
                ManifestVerifier.CODE_PAYLOAD_MANIFEST_CONTAINS_INVALID_FILE,
                filepath)) {
            return true;
        }
        return false;

    }

    public static boolean containsObject(final SimpleResult result,
            final String code, final String object) {
        final List<SimpleMessage> messages =
                result.getSimpleMessagesByCode(code);
        for (final SimpleMessage message : messages) {
            if (message.getObjects().contains(object)) {
                return true;
            }
        }
        return false;
    }

    public static Set<String> aggregateObjects(final SimpleResult result,
            final String code) {
        final Set<String> objects = new HashSet<String>();
        final List<SimpleMessage> messages =
                result.getSimpleMessagesByCode(code);
        for (final SimpleMessage message : messages) {
            objects.addAll(message.getObjects());
        }
        return objects;
    }

    public static Set<String> aggregateSubjects(final SimpleResult result,
            final String code) {
        final Set<String> subjects = new HashSet<String>();
        final List<SimpleMessage> messages =
                result.getSimpleMessagesByCode(code);
        for (final SimpleMessage message : messages) {
            subjects.add(message.getSubject());
        }
        return subjects;

    }

    public static Set<String> getInvalidTagFiles(final SimpleResult result) {
        return aggregateObjects(result,
                ManifestVerifier.CODE_TAG_MANIFEST_CONTAINS_INVALID_FILE);
    }

    public static Set<String> getInvalidPayloadFiles(final SimpleResult result) {
        return aggregateObjects(result,
                ManifestVerifier.CODE_PAYLOAD_MANIFEST_CONTAINS_INVALID_FILE);
    }

    public static Set<String> getMissingTagFiles(final SimpleResult result) {
        return aggregateObjects(result,
                CompleteVerifier.CODE_TAG_MANIFEST_CONTAINS_MISSING_FILE);
    }

    public static Set<String> getMissingPayloadFiles(final SimpleResult result) {
        return aggregateObjects(result,
                CompleteVerifier.CODE_PAYLOAD_MANIFEST_CONTAINS_MISSING_FILE);
    }

    public static Set<String> getExtraPayloadFiles(final SimpleResult result) {
        return aggregateSubjects(result,
                CompleteVerifier.CODE_PAYLOAD_FILE_NOT_IN_PAYLOAD_MANIFEST);
    }

}
