<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="data3">
<data4>
  <xsl:apply-templates/>
  <data4_body></data4_body>
</data4>
</xsl:template>

<xsl:template match="data3_title">
<data4_title><xsl:apply-templates/></data4_title>
</xsl:template>

</xsl:stylesheet>

