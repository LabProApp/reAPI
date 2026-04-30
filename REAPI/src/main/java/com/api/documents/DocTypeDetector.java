package com.api.documents;

public class DocTypeDetector {

	
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
