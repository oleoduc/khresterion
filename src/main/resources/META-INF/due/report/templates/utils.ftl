<#macro renderTitle1 title>
	<fo:block ${style.title1()}>
		${title}
   </fo:block>
</#macro>

<#macro renderTitle2 title>
	<fo:block ${style.title2()}>
		${title}
	</fo:block>
</#macro>

<#macro renderSummary summary>
	<#local result = ''>
	<#list summary as summaryItem>
		<#if summaryItem != PropertyWrapper.DEFAULT_VALUE>
			<#if result != ''>
				<#local separator = '${utils.getCircle()}'>
			<#else>
				<#local separator = PropertyWrapper.DEFAULT_VALUE>
			</#if>
			<#local result = result + separator + summaryItem>
		</#if>
	</#list> 
	${result}	
</#macro>

<#macro separator>
	<fo:block space-before="6pt"  space-after="10pt" text-align="left">
		<fo:inline font-size="10pt" color="${style.grey()}">
			..................................................................................................................................................................................
		</fo:inline>
	</fo:block>
</#macro>

<#macro breakline>
<fo:block white-space-treatment="preserve" white-space-collapse="false" 
linefeed-treatment="preserve">

</fo:block> 
</#macro>

<#-- ================================================   FUNCTIONS ==================================================== -->
<#function i18n key>
	<#return kbuilderUtils.i18n(key)>
</#function>

<#function getPipe>
	<#return '<fo:inline font-weight="bold" font-size="11pt" color="rgb(200,200,200)" letter-spacing="0.5pt"> | </fo:inline>'>
</#function>


<#function getCircle>
	<#return '<fo:inline font-size="10pt" color="rgb(200,200,200)" letter-spacing="0.5pt"> • </fo:inline>'>
</#function>

<#function nullValue>
	<#return '?'>
</#function>

<#function colon>
	<#return ' : '>
</#function>

<#function hyphen>
	<#return ' - '>
</#function>


<#function equals>
	<#return ' = '>
</#function>