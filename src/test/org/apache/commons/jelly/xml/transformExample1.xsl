<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="document">
<data1>
  <xsl:apply-templates/>
  <data1_body></data1_body>
</data1>
</xsl:template>

<xsl:template match="chapter">
<data1_title><xsl:apply-templates/></data1_title>
</xsl:template>

</xsl:stylesheet>

