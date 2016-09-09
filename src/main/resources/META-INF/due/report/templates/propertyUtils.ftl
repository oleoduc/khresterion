<#-- ======================== MACROS ===================================== -->
<#macro renderProperty property 
					   label=PropertyWrapper.getName(property)
			    	   separator=valueSeparator()
					   unit=''>
	${getPropertyAsString(property, label, separator, unit)}
</#macro>

<#macro renderLabelAndValue label value>
	${Encodings.quoteCharactersInto(label)} ${applyValueStyle(Encodings.quoteCharactersInto(value))}	
</#macro>

<#macro renderNotEmpty property label=PropertyWrapper.getName(property)
                       separator=valueSeparator()
                       unit=''>
   <#if PropertyWrapper.getValue(property) != PropertyWrapper.DEFAULT_VALUE>
    ${getPropertyAsString(property, label, separator, unit)}
    </#if>
</#macro>

<#-- ================================================   FUNCTIONS ==================================================== -->


<#function applyValueStyle value>
	<#local result = ''>
	<#if isNotNullValue(value)>
		<#local result = '<fo:inline ${style.propertyValue()}>${value}</fo:inline>'>
	</#if> 
	<#return result>
</#function>

<#function getPropertyAsString property 
					   		   label=PropertyWrapper.getName(property)
			    	   		   separator=valueSeparator()
					   		   unit=''
					   		   nullValue=PropertyWrapper.DEFAULT_VALUE>
	
	<#local labelAndSeparator = ''>
	<#if (label != '')>
		<#local labelAndSeparator = label + separator>
	</#if> 
	
	<#local value = PropertyWrapper.getValue(property)>
	<#if value != PropertyWrapper.DEFAULT_VALUE>
		<#local valueAndUnit = value + ' ' + unit>
	<#else>
		<#local valueAndUnit = nullValue>
	</#if>
	
	<#return '${labelAndSeparator}${applyValueStyle(valueAndUnit)}'>
</#function>

<#function getPropertyAsStringOrEmpty property 
					   		   label=PropertyWrapper.getName(property)
			    	   		   separator=valueSeparator()
					   		   unit=''>
	
	<#local result = ''>
	<#local value = PropertyWrapper.getValue(property)>
	<#if value != PropertyWrapper.DEFAULT_VALUE>
		<#local labelAndSeparator = ''>
		<#if (label != '')>
			<#local labelAndSeparator = label + separator>
		</#if> 
		<#local valueAndUnit = value + ' ' + unit>
		<#local result = '${labelAndSeparator}${applyValueStyle(valueAndUnit)}'>
	</#if>
	
	<#return result>
</#function>

<#function getPropertyWithUnitAsString property unit>
	<#return getPropertyAsString(property, PropertyWrapper.getName(property), valueSeparator(), unit, utils.nullValue())>
</#function>

<#function getValueWithUnitAsString property unit>
	<#return getPropertyAsString(property, '', '', unit, '')>
</#function>

<#function getValueAsString property>
	<#return getPropertyAsString(property, '', '', unit, '')>
</#function>


<#function isPropertyValuate property>
	<#return isNotNullValue(PropertyWrapper.getValue(property))>
</#function>

<#function isNotNullValue value>
	<#return !isNullValue(value)>
</#function>


<#function isNullValue value>
	<#return value == PropertyWrapper.DEFAULT_VALUE>
</#function>

<#function valueSeparator>
	<#return utils.colon()>
</#function>
