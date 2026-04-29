package com.api.documents;

/**
 * Utility class that maps a MIME type string to a human-readable document
 * type label used throughout the documents module.
 *
 * <p>The mapping rules are applied in priority order:
 * <ol>
 *   <li>Any MIME type starting with {@code "image/"} → {@code "IMAGE"}</li>
 *   <li>Any MIME type starting with {@code "video/"} → {@code "VIDEO"}</li>
 *   <li>{@code "application/pdf"} → {@code "PDF"}</li>
 *   <li>Any MIME type containing {@code "word"} → {@code "DOC"}</li>
 *   <li>Any MIME type containing {@code "text"} → {@code "TEXT"}</li>
 *   <li>Any MIME type containing {@code "excel"} or {@code "spreadsheet"}
 *       → {@code "EXCEL"}</li>
 *   <li>Anything else → {@code "OTHER"}</li>
 *   <li>{@code null} input → {@code "UNKNOWN"}</li>
 * </ol>
 *
 * <p>This class is not instantiable; use {@link #detect(String)} directly.
 */
public class DocTypeDetector {

	/**
	 * Detects the human-readable document type from a MIME content-type string.
	 *
	 * <p>The detection is case-insensitive. A {@code null} argument returns
	 * {@code "UNKNOWN"} rather than throwing an exception. An unrecognised but
	 * non-null MIME type returns {@code "OTHER"}.
	 *
	 * @param contentType the MIME type string (e.g. {@code "image/jpeg"},
	 *                    {@code "application/pdf"}); may be {@code null}
	 * @return one of {@code "IMAGE"}, {@code "VIDEO"}, {@code "PDF"},
	 *         {@code "DOC"}, {@code "TEXT"}, {@code "EXCEL"}, {@code "OTHER"},
	 *         or {@code "UNKNOWN"} if {@code contentType} is {@code null}
	 */
	public static String detect(String contentType) {

		if (contentType == null) {
			return "UNKNOWN";
		}

		contentType = contentType.toLowerCase();

		if (contentType.startsWith("image/")) {
			return "IMAGE";
		}
		if (contentType.startsWith("video/")) {
			return "VIDEO";
		}
		if (contentType.equals("application/pdf")) {
			return "PDF";
		}
		if (contentType.contains("word")) {
			return "DOC";
		}
		if (contentType.contains("text")) {
			return "TEXT";
		}
		if (contentType.contains("excel") || contentType.contains("spreadsheet")) {
			return "EXCEL";
		}

		return "OTHER";
	}
}
