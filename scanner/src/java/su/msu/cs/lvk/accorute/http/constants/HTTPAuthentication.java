package su.msu.cs.lvk.accorute.http.constants;

public enum HTTPAuthentication {
    HTTP_BASIC,
    HTTP_DIGEST,
    HTTP_FORM,
    HTTP_NTLM;

    public static HTTPAuthentication parseHttpAuthenticationType(String wwwAuthenticateHeader) {
        if (wwwAuthenticateHeader != null) {
            if (wwwAuthenticateHeader.trim().startsWith("Basic")) return HTTP_BASIC;
            if (wwwAuthenticateHeader.trim().startsWith("Digest")) return HTTP_DIGEST;
        }

        return null;
    }
}
