<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="document">
  <html>
    <xsl:apply-templates/>
    <body></body>
  </html>
</xsl:template>

<xsl:template match="chapter">
  <title><xsl:apply-templates/></title>
</xsl:template>

</xsl:stylesheet>
