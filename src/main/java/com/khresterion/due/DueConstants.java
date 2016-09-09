/**
 * copyright Khresterion 2016
 */
package com.khresterion.due;

/**
 * @author khresterion
 *
 */
public interface DueConstants {

  public static final String TYPE_PLAN = "Ontologie1_Document";

  public static final String TYPE_SECTION = "Ontologie1_Section0";

  public static final String TYPE_REDACTION = "Ontologie1_Redaction";

  public static final String TYPE_TEXTE = "Ontologie1_Texte";

  public static final String LINK_PLAN_SECTION =
      "Ontologie1_Document/Ontologie1_estComposeDe/Ontologie1_Section0";

  public static final String LINK_ETUDE_PLAN =
      "Ontologie1_DUE/Ontologie1_estRepresentePar/Ontologie1_DocumentDUE0";

  public static final String PLAN_REDACTION =
      "Ontologie1_Document/Ontologie1_estRepresentePar/Ontologie1_Redaction";

  public static final String SECTION_REDACTION =
      "Ontologie1_Section0/Ontologie1_estRepresentePar/Ontologie1_Redaction";

  public static final String SECTION_TEXTE =
      "Ontologie1_Section0/Ontologie1_estComposeDe/Ontologie1_Texte";

  public static final String TYPE_ETUDE = "Ontologie1_DUE";

  public static final String TYPE_DOCUMENTDUE = "Ontologie1_DocumentDUE0";

  public static final String LINK_SECTION_SUIVANT =
      "Ontologie1_Section0/Ontologie1_estSuiviDe/Ontologie1_Section0";

  public static final String LINK_SECTION_COMPOSANT =
      "Ontologie1_Section0/Ontologie1_aPourComposant/Ontologie1_Section0";

  public static final String LINK_SECTION_ALTERNATIVE =
      "Ontologie1_Section0/Ontologie1_aPourAlternative/Ontologie1_Section0";

  public static final String[] SECTION_TYPES = {
      "Ontologie1_00Entete","Ontologie1_091bCotisationIsoleeEnPPSS0",
      "Ontologie1_131ModificationDeRegime",
      "Ontologie1_032b8SalariesBeneficiairesRedactionBCritere2Remuneration3PlafondSS",
      "Ontologie1_13DureeModificationDenonciation",
      "Ontologie1_093bCotisationisolefamilleObligatoireEnPPSS",
      "Ontologie1_051DerogationsIntroduction", "Ontologie1_EvolutionUlterieureDesCotisations",
      "Ontologie1_093cCotisationisolefamilleObligatoireEnPDuSalaire",
      "Ontologie1_094cCotisationisolefamilleFacultativeEnPDuSalaire",
      "Ontologie1_032b3SalariesBeneficiairesRedactionBCritere2Tranches1Et2",
      "Ontologie1_032b7SalariesBeneficiairesRedactionBCritere2Remuneration2PlafondSS",
      "Ontologie1_DerogationLesSalariesEtApprentisSousContratADureeDetermineeOuContratDeMissionDuneDureeInferieureA12Mois",
      "Ontologie1_032a6SalariesBeneficiairesRedactionBNoncadresA6NonArticles44BisEt36",
      "Ontologie1_041CaractereObligatoireDeLadhesionDesSalariesMiseEnPlaceDuRegime",      
      "Ontologie1_032b9SalariesBeneficiairesRedactionBCritere2Remuneration4PlafondSS",
      "Ontologie1_Garanties",
      "Ontologie1_032a8SalariesBeneficiairesRedactionBNoncadresA8NonArticle4",
      "Ontologie1_InformationCollective",
      "Ontologie1_032a2SalariesBeneficiairesRedactionBCadresA2Articles4Et4bis",
      "Ontologie1_052SalariesEtApprentisSousContratADureeDetermineeOuContratDeMissionDuneDureeInferieureA12Mois",
      "Ontologie1_132CreationDeRegime", "Ontologie1_03SalariesBeneficiaires",
      "Ontologie1_06SalariesDontLeContratDeTravailEstSuspendu", "Ontologie1_08Garanties",
      "Ontologie1_InformationIndividuelle", "Ontologie1_054SalariesATempsPartielEtApprentis",
      "Ontologie1_032a3SalariesBeneficiairesRedactionBCadresA3Articles44bisEt36",
      "Ontologie1_092aCotisationFamilialeUniqueEnEuros",
      "Ontologie1_DerogationLesSalariesBeneficiantEnQualiteDayantsDroit",
      "Ontologie1_042aCaractereObligatoireDeLadhesionDesSalariesModificationDuRegime",
      "Ontologie1_031SalariesBeneficiairesRedactionA",
      "Ontologie1_093aCotisationisolefamilleObligatoireEnEuros",
      "Ontologie1_094aCotisationisolefamilleFacultativeEnEuros",
      "Ontologie1_032a5SalariesBeneficiairesRedactionBNoncadresA5NonAGIRC",
      "Ontologie1_055SalariesQuiSontBeneficiairesDeLaideALacquisitionDuneComplementaireSante",
      "Ontologie1_01bPreambuleModificationDuRegime",
      "Ontologie1_092cCotisationFamilialeUniqueEnPDuSalaire0",
      "Ontologie1_032bSalariesBeneficiairesRedactionBTranchesDeRemuneration",
      "Ontologie1_091aCotisationIsoleeEnEuros0",
      "Ontologie1_032b1SalariesBeneficiairesRedactionBCritere2Tranche1OuA",
      "Ontologie1_07SalariesDontLeContratDeTravailEstRompuPortabilite",
      "Ontologie1_056SalariesBeneficiantEnQualiteDayantsDroitOuDansLeCadreDunAutreEmploiDuneCouvertureCollectiveDeRemboursementDeFraisDeSante",
      "Ontologie1_033SalariesBeneficiairesRedactionC",
      "Ontologie1_032aSalariesBeneficiairesRedactionBStatut",
      "Ontologie1_SalariesDontLeContratDeTravailEstSuspendu",
      "Ontologie1_094bCotisationisolefamilleFacultativeEnPPSS",
      "Ontologie1_032b5SalariesBeneficiairesRedactionBCritere2TranchesABEtC",
      "Ontologie1_DerogationLesSalariesQuiSontBeneficiairesDeLaideALacquisitionDuneComplementaireSante",
      "Ontologie1_032a1SalariesBeneficiairesRedactionBCadresA1Article4",
      "Ontologie1_11InformationIndividuelle",
      "Ontologie1_032a7SalariesBeneficiairesRedactionBNoncadresA7NonArticles4Et4Bis",
      "Ontologie1_057SalariesCouvertsParUneAssuranceIndividuelle",
      "Ontologie1_021ObjetDeLengagementDeLemployeurDesignationDeLorganismeAssureur",
      "Ontologie1_09Cotisations", "Ontologie1_032a4SalariesBeneficiairesRedactionBCadresA4AGIRC",
      "Ontologie1_12InformationCollective",
      "Ontologie1_032b6SalariesBeneficiairesRedactionBCritere2TrancheBEtouCTranche2",
      "Ontologie1_091cCotisationIsoleeEnPDuSalaire", "Ontologie1_02ObjetDeLengagementDeLemployeur",
      "Ontologie1_01aPreambuleMiseEnPlaceInitialeDuRegime",
      "Ontologie1_034ConditionDanciennetePourLaccesAuRegime0", "Ontologie1_Section0",
      "Ontologie1_032b4SalariesBeneficiairesRedactionBCritere2TranchesAEtB",
      "Ontologie1_092bCotisationFamilialeUniqueEnPPSS0",
      "Ontologie1_032b2SalariesBeneficiairesRedactionBCritere2Remuneration2PlafondsSS",
      "Ontologie1_SalariesDontLeContratDeTravailEstRompuPortabilite",
      "Ontologie1_10EvolutionUlterieureDesCotisations",
      "Ontologie1_DerogationLesSalariesEtApprentisSousContratADureeDetermineeOuContratDeMissionDuneDureeAuMoinsEgaleA12Mois",
      "Ontologie1_DerogationLesSalariesATempsPartielEtApprentisDontLadhesion",
      "Ontologie1_053SalariesEtApprentisSousContratADureeDetermineeOuContratDeMissionDuneDureeAuMoinsEgaleA12Mois",
      "Ontologie1_DerogationLesSalariesCouvertsParUneAssuranceIndividuelle",
      "Ontologie1_042bCaractereObligatoireDeLadhesionDesSalariesModificationDuRegimeEtCreationDuneCotisationSalariale",
      "Ontologie1_DerogationsAuCaractereObligatoire"};

  public static final String PATH_CRITERIA = "Ontologie1_estComposeDe/Ontologie1_Texte";
  
  public static final String DEFAULT_DUE_ID = "777";
  
  public static final String SESSION_PLAN = "DUE.SESSION.PLAN";
  
  public static final String SECTION__EST_PRESENTE = "Ontologie1_Section0/Ontologie1_aPourPropriete/Ontologie1_EstPresente";

  public static final String SECTION_EST_INCLUSE = "Ontologie1_Section0/Ontologie1_aPourPropriete/Ontologie1_EstIncluse";
}
