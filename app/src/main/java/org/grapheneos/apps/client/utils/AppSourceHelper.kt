package org.grapheneos.apps.client.utils

class AppSourceHelper {

    companion object {
        private const val gos = "GrapheneOS"
        private const val buildByGos = "GrapheneOS build"
        private const val google = "Google (mirror)"


        /*package name, category name, */
        private val listOfKnownApps = mutableMapOf<String, String>().apply {
            put("app.attestation.auditor", gos)
            put("app.grapheneos.apps", gos)
            put("app.grapheneos.camera", gos)
            put("app.grapheneos.pdfviewer", gos)

            put("com.google.android.gms", google)
            put("com.google.android.gsf", google)
            put("com.android.vending", google)

        }

        fun getCategoryName(pkgName: String) = listOfKnownApps.getOrDefault(pkgName, buildByGos)
    }

}
