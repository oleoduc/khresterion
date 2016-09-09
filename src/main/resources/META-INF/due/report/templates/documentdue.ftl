<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE fo:root [
<!ENTITY sup2 "&#178;">
<!ENTITY amp "&#38;">
<!ENTITY ndash "&#8211;">
]>

<#import "/style.ftl" as style>
<#import "/kbuilderUtils.ftl" as kbuilderUtils>
<#import "/utils.ftl" as utils>
<#import "/propertyUtils.ftl" as propertyUtils>
<#import "/reportUtils.ftl" as reportUtils>

<#-- Global vars -->

<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <fo:layout-master-set background-color="rgb(255,255,255)">
        <fo:simple-page-master master-name="page"
                               page-width="21cm" 
                               page-height="29.7cm" 
                               margin-top="1cm" 
                               margin-left="1cm"
                               margin-right="1cm"
                               margin-bottom="1cm"                               
                               background-color="rgb(255,255,255)">
            <fo:region-body margin-top="1cm" margin-bottom="1cm">
            
            </fo:region-body>
            <fo:region-before region-name="xsl-region-before" extent="1cm"/>
            <fo:region-after region-name="xsl-region-after" extent="1cm"/>
        </fo:simple-page-master>
    </fo:layout-master-set>

    <fo:page-sequence master-reference="page">
    <fo:static-content flow-name="xsl-region-before">
    <fo:block font="10pt Calibri" color="black" line-height="12pt" text-align="right">fiche synthese &#169; Khresterion</fo:block>
    </fo:static-content>
    <fo:static-content flow-name="xsl-region-after">
    <fo:block font="10pt Calibri" color="black" line-height="12pt" text-align="right"> page <fo:page-number/></fo:block>
    </fo:static-content>
        <fo:flow flow-name="xsl-region-body" ${style.page()} >

<#-- TITRE -->
            <@reportUtils.renderTitle 'Fiche de synthèse' '' 'v0.3'/>

<#-- chaque violation de regle doit etre argumentee + commentaire -->
<#-- CALK STOPPED -->
            <@reportUtils.renderCalkStopped />
            
            <@synthese.renderDateEtude />          

<#-- SOUSCRIPTEUR -->
<fo:block>
    <@utils.renderTitle1 '0. Identification du souscripteur' />
    <fo:block>
    <@synthese.renderSouscripteur />
    </fo:block>
</fo:block>

<#-- PROFIL ENTREPRISE -->
<fo:block margin-top="0.5cm">
    <@utils.renderTitle1 'I. Profil entreprise' />
    <@profilgeneral.renderProfilGeneralDeLentreprise />
    <@utils.renderTitle2 'Commentaires' />
    <@profilgeneral.profilGeneralComment />
</fo:block>

<#-- CONTEXTE COMMERCIAL-->
<fo:block margin-top="0.5cm">
    <@utils.renderTitle1 'II. Contexte commercial' />
    <@ccommercial.renderContexteCommercial />
    <@utils.renderTitle2 'Commentaires' />
    <@ccommercial.renderCommercialComment />
</fo:block>

<#-- ANALYSE ACTIVITE -->
 <fo:block margin-top="0.5cm"> 
    <@utils.renderTitle1 'III. Analyse activité' />
    <@activite.renderGeneralActivite />
    <@utils.renderTitle2 'Activités' />
    <@activite.renderAnalyseActivite />
    <@utils.breakline />
    <@utils.renderTitle2 'Produit/Clients/Fournisseurs' />
    <@activite.renderActiviteProduit />
    <@utils.breakline />
    <@utils.renderTitle2 'Commentaires' />
    <@activite.renderActiviteComment />           
</fo:block>
   
<#-- ANALYSE SINISTRALITES -->
<fo:block page-break-before="always">           
    <@utils.renderTitle1 'IV. Sinistralité' />
    <@sinistralite.renderAnalyseDeLaSinistralite />
    <@utils.breakline />
    
    <@utils.renderTitle1 'Statistiques sinistres' />
    <@sinistralite.tableauSinistralite />

    <@utils.breakline />
    <@utils.renderTitle2 'Commentaires' />
    <@sinistralite.renderSinistraliteComment />                                 
</fo:block>

<#-- RISQUES ET GARANTIE -->
 <fo:block page-break-before="always">

        <@utils.renderTitle1 'V. Garanties' />
        <@revuerisques.renderRevuesDesRisques />
        <@utils.renderTitle2 'Commentaires' />
        <@revuerisques.renderRisqueComment />
</fo:block>

<#-- CONCLUSION -->
<fo:block page-break-before="always">  
        <@utils.renderTitle1 'VI. Conclusion' />
        <@utils.renderTitle2 'Synthèse' />
        <@synthese.renderConclusion />
        <@utils.renderTitle2 'Commentaires' />
        <@synthese.renderSyntheseComment />

 </fo:block> 

        </fo:flow>
    </fo:page-sequence>
</fo:root>



