/**
 * Created by admin_000 on 18/07/2016.
 */

// listSections : liste des objets présents dans le JSon, obtenue depuis planlist.js
var listSections;
var update = true;
// objJsonMap : fait le lien entre l'idDue (l'id de la section comme elle est notée dans la due) et l'objet Json de cette section.
var objJsonMap = new Map()
// idMap : fait le lien entre l'id web d'une section et son idDue
var idMap = new Map()
// nameSections : liste des noms de toutes les sections présentes dans le JSon (pour les faire apparaître dans la popup lors de l'ajout d'une section dans la treeview).
var nameSections = []
// typeSection : variable qui sert à determiner depuis quel bouton est généré l'ajout d'une section
// 1 : on veut générer une section suivante
// 2 : on veut générer une section alternative
// 3 : on veut générer une section composée
var typeSection = 0
// idSectionToAddTo : l'idDue de la section à laquelle on veut ajouter une section suivante (utilisée lors des requêtes POST)
var idSectionToAddTo
// idSectionToAdd : l'idDue de la section suivante à ajouter (utilisée lors des requêtes POST)
var idSectionToAdd
// id web des sections, incrémentée à chaque ajout d'une nouvelle section.
var id = 0

xMousePosition = 0
yMousePosition = 0

treeviewExec = function(){
    var lvl = 0
    // lvlMap : chaque section + les alternatives correspondantes se situent sur un même niveau
    var lvlMap = new Map()

    if (update) {
        // On stocke les objets de listSections dans une map pour qu'ils puissent être retrouvés facilement par leur idDue
        objJsonMap.length = 0
        for (var s in listSections) {
            objJsonMap.set(listSections[s]["id"], listSections[s])
        }
    }

    // Getters et setters
    function updateId(update) {
        if (update)
            return id++
        else
            return id
    }

    function updateLevel(update) {
        if (update)
            return ++lvl
        else
            return lvl
    }

    // On cherche l'objet correspondant à l'entete dans listSections (objets du JSon)
    var entete

    for(idx in listSections){
        if(listSections[idx]["label"] == "00 - Entête 8"){
            entete = listSections[idx]
        }
        nameSections.push(listSections[idx]["label"])
    }

    // demo treeObject, c'est la base de la structure et l'entrée de la treeview.
    var treeObject = {
        id: updateId(true),
        name: "Treeview",
        level: 0,
        alts: [],
        children: [],
        nameSec: nameSections,
        isAlt: false,
        isComp: false,
        newAltPresent: false
    }

    // On créé une section qui a pour nom celui de l'entete pour la mettre dans la treeview.
    // A remarquer que l'entete est placée dans les fils de l'objet treeObject et ne correspond pas à treeObject lui-même.
    // Ainsi, si l'entete possède une section qui la suit, on pourra la push dans ce même tableau des fils de treeObject.
    treeObject.children.push({
        id: updateId(true),
        idDue: entete["id"],
        name: entete["label"].slice(0, 15) + "...",
        fullname: entete["label"],
        level: updateLevel(true),
        alts: [],
        newAlt: '>',
        children: [],
        nameSec: nameSections,
        isAlt: false,
        isComp: true,
        newAltPresent: true
    })

    // initTree : On initialise notre treeview à partir de l'entete (objet présent dans listSections (JSon)) : cf appel de la fonction initTree (2ème argument).
    // On regarde si l'entete possède une (section) alternative, une (section) composée et/ou une (section) suivante.
    // On les ajoute à la treeview et on rappelle la fonction récursivement, pour chaque section trouvée.
    function initTree(objTree, objJson, parentObjTree){
        if(objJson["bo"]["aPourAlternativeSection0"]["value"] !== null){
            alt = {
                id: updateId(true),
                idDue: objJsonMap.get(objJson["bo"]["aPourAlternativeSection0"]["value"])["id"],
                name: objJsonMap.get(objJson["bo"]["aPourAlternativeSection0"]["value"])["label"].slice(0, 15) + "...",
                fullname: objJsonMap.get(objJson["bo"]["aPourAlternativeSection0"]["value"])["label"],
                level: objTree.level,
                children: [],
                nameSec: nameSections,
                isAlt: true,
                isComp: false,
                newAltPresent: false
            }
            objTree.alts.push(alt)
            idMap.set(alt.id, alt.idDue)
            lvlMap.get(alt.level).push(alt.id + 1)
            initTree(alt, objJsonMap.get(objJson["bo"]["aPourAlternativeSection0"]["value"]), parentObjTree)
        }
        if(objJson["bo"]["aPourComposantSection0"]["value"] !== null){
            comp = {
                id: updateId(true),
                idDue: objJsonMap.get(objJson["bo"]["aPourComposantSection0"]["value"])["id"],
                name: objJsonMap.get(objJson["bo"]["aPourComposantSection0"]["value"])["label"].slice(0, 15) + "...",
                fullname: objJsonMap.get(objJson["bo"]["aPourComposantSection0"]["value"])["label"],
                level: updateLevel(true),
                alts: [],
                newAlt: '>',
                children: [],
                nameSec: nameSections,
                isAlt: false,
                isComp: true,
                newAltPresent: true
            }
            objTree.children.push(comp)
            idMap.set(comp.id, comp.idDue)
            lvlMap.set(comp.level, [comp.id + 1])
            initTree(comp, objJsonMap.get(objJson["bo"]["aPourComposantSection0"]["value"]), objTree)
        }
        if(objJson["bo"]["estSuiviDeSection0"]["value"]){
            next = {
                id: updateId(true),
                idDue: objJsonMap.get(objJson["bo"]["estSuiviDeSection0"]["value"])["id"],
                name: objJsonMap.get(objJson["bo"]["estSuiviDeSection0"]["value"])["label"].slice(0, 15) + "...",
                fullname: objJsonMap.get(objJson["bo"]["estSuiviDeSection0"]["value"])["label"],
                level: updateLevel(true),
                alts: [],
                newAlt: '>',
                children: [],
                nameSec: nameSections,
                isAlt: false,
                isComp: false,
                newAltPresent: true
            }
            parentObjTree.children.push(next)
            idMap.set(next.id, next.idDue)
            lvlMap.set(next.level, [next.id + 1])
            initTree(next, objJsonMap.get(objJson["bo"]["estSuiviDeSection0"]["value"]), parentObjTree)
        }
    }

    initTree(treeObject.children[0], entete, treeObject);

    // Une section et ses alternatives se trouvent sur un même niveau (level)
    // map : A chaque niveau est associé un tableau contenant une section et ses alternatives
    // Cela nous permettra de rendre la treeview dynamique :
    // Lorsqu'on clique sur une section, on affiche ses sous-sections et on cache les sous-sections de ses alternatives.
    // (Et lorsqu'on clique sur une alternative, on cache le détail de la section et des autres alternatives du même niveau)
    function initMap(data) {
        idMap.set(data.id+1, data.idDue)
        if (data.children.length != 0) {
            lvlMap.set(data.level, [data.id + 1])
            data.children.forEach(function (item, index, array) {
                initMap(item)
            })
        }
        if (data.alts !== 'undefined' && typeof(data.alts.children) !== 'undefined'){
            data.alts.forEach(function (item, index, array) {
                lvlMap.get(data.level).push(item.id + 1)
            })
        }
        if(data.alts !== 'undefined'){
            data.alts.forEach(function (item, index, array) {
                idMap.set(item.id+1, item.idDue)
            })
        }
    }

    // resolveIdDue : on récupère l'idDue d'une section grâce à son nom complet
    // On utilise cette fonction lorsqu'on doit choisir une section depuis la fenêtre popup.
    // On s'en sert lors des requêtes POST
    function resolveIdDue(name){
        for(idx in listSections){
            if(listSections[idx]["label"] == name){
                return listSections[idx]["id"]
            }
        }
    }

    // define the item component, cf Vue.js
    Vue.component('item', {
        // template tag : Does not get rendered when the page is loaded. Instead, Stores markup on the client-side
        // so we can grab it and use it when we need it. Here, we reference the <item> tag and use it in treeview.html
        template: '#item-template',
        // props : permet de passer des données à l'instance d'un component. Dans notre cas, chaque component item peut
        // utiliser la variable model.
        // Ici, pour lier le model à treeObject, on commence par définir "treeData: treeObject" dans l'objet data de Vue
        // Ensuite, dans treeview.html, on fait le lien :model="treeData".
        // Enfin, on peut l'utiliser ici, en faisant appel à this.model mais également dans les balises <item> par un appel direct à model.
        props: {
            model: Object
        },
        // variables booléennes nous permettant de faire apparaître / disparaître les fenêtres définies dans treeview.html
        data: function () {
            return {
                open: false,
                showCtx: false,
                showDetails: false
            }
        },
        computed: {
            isFolder: function () {
                return this.model.children &&
                    this.model.children.length
            },
            isAlt: function () {
                return this.model.alts &&
                    this.model.alts.length
            }
        },
        methods: {
            show: function () {
                if(objJsonMap.get(idMap.get(this.model.id+1))["bo"]["aPourProprieteEstPresente"]["isAdvised"]){
                    $('#details'+ (this.model.id+1) + " ul li div.presence").css('color', 'green')
                }
                if(objJsonMap.get(idMap.get(this.model.id+1))["bo"]["aPourProprieteEstIncluse"]["isClassAdvised"]){
                    $('#details'+ (this.model.id+1) + " ul li div.estInclu").css('color', 'green')
                }
                if(objJsonMap.get(idMap.get(this.model.id+1))["bo"]["aPourProprieteAUneAlternativeIncluse"]["isAdvised"]){
                    $('#details'+ (this.model.id+1) + " ul li div.altInclu").css('color', 'green')
                }
                sectionId = this.model.id + 1
                if(typeof(lvlMap.get(this.model.level)) !== 'undefined'){
                    lvlMap.get(this.model.level).forEach(function (item, index, array) {
                        if (sectionId !== item) {
                            $('#' + item).css("display", "none")
                        }
                        else {
                            $('#' + item).css("display", "block")
                        }
                    })
                }
                this.showDetails = !this.showDetails
            },
            closeDetails: function () {
                this.showDetails = false
            },
            showModal: function(fromSection){
                this.open = true
                if(fromSection){
                    typeSection = 1
                    idSectionToAddTo = idMap.get(this.$children[this.$children.length-1].model.id+1)
                }
                else if(fromSection == false){
                    typeSection = 2
                    idSectionToAddTo = idMap.get(this.model.id+1)
                }
                else{// undefined
                    typeSection = 3
                    idSectionToAddTo = idMap.get(this.model.id+1)
                }
            },
            closeModal: function(){
                this.open = false
            },
            addSection: function (nameButton) {
                idSectionToAdd = resolveIdDue(nameButton)
                if(typeSection == 1){
                    this.model.children.push({
                        id: updateId(true),
                        idDue: idSectionToAdd,
                        name: nameButton.slice(0, 15) + "...",
                        fullname: nameButton,
                        level: updateLevel(true),
                        alts: [],
                        newAlt: '>',
                        children: [],
                        nameSec: nameSections,
                        isAlt: false,
                        isComp: false,
                        newAltPresent: true
                    })
                    addEstSuiviDePost()
                }
                else if(typeSection == 3){
                    Vue.set(this.model, 'children', [])
                    this.model.children.push({
                        id: updateId(true),
                        idDue: idSectionToAdd,
                        name: nameButton.slice(0, 15) + "...",
                        fullname: nameButton,
                        level: updateLevel(true),
                        alts: [],
                        newAlt: '>',
                        children: [],
                        nameSec: nameSections,
                        isAlt: false,
                        isComp: true,
                        newAltPresent: true
                    })
                    addAPourComposantePost()
                }
                else{
                    this.model.alts.push({
                        id: updateId(true),
                        idDue: idSectionToAdd,
                        name: nameButton.slice(0, 15) + "...",
                        fullname: nameButton,
                        level: this.model.level,
                        children: [],
                        nameSec: nameSections,
                        isAlt: true,
                        isComp: false,
                        newAltPresent: false
                    })
                    addAPourAlternativePost()
                }
                this.open = false
                if (lvlMap.has(this.model.level)) {
                    lvlMap.get(this.model.level).push(this.model.id + 1)
                }
                else {
                    lvlMap.set(this.model.level, [this.model.id + 1])
                }

                console.log(id)
                idMap.set(id, idSectionToAdd)
            },
            menu: function(){
                this.showCtx = true
            },
            closeCtx: function() {
                this.showCtx = false
            },
            removeSection: function(idToRemove){
                this.showCtx = false
                console.log(this.model.isAlt)
                if(this.model.isAlt){
                    var index = this.$parent.model.alts.indexOf(this.model);
                    if (index > -1) {
                        for (var i=0; i<lvlMap.get(this.model.level).length; i++) {
                            if(lvlMap.get(this.model.level)[i] === this.model.id+1){
                                lvlMap.get(this.model.level).splice(i, 1)
                                break
                            }
                        }
                        this.$parent.model.alts.splice(index, 1);
                    }
                    removeAltPost(this.$parent.model.idDue)
                } else {
                    var index = this.$parent.model.children.indexOf(this.model);
                    if (index > -1) {
                        lvlMap.delete(this.model.level)
                        this.$parent.model.children.splice(index, 1);
                    }
                    console.log(this.model.isComp)
                    if(this.model.isComp){
                        removeCompPost(this.$parent.model.idDue)
                    }else{
                        removeSuivPost(this.$parent.model.children[index-1].idDue)
                    }
                }
                idMap.delete(idToRemove)
            }
        }
    });

    // boot up the demo
    var demo = new Vue({
        el: '#demo',
        data: {
            treeData: treeObject,
        }
    })

    initMap(treeObject)
}

if(update){
    // promise, get asynchrone.
    $.ajax({
        url: '/due/' + 'v2/instance/search?envkey=' + window.envkey
        + '&type_id=Ontologie1_Section0', dataType: 'json'
    }).done(function(data2) {
        listSections = data2
        treeviewExec(data2);
    });
}
else{
    treeviewExec();
}

// requêtes Serveur
function addEstSuiviDePost(){
    $.ajax({
        url: '/due/' + 'v2/instance/' + idSectionToAddTo + '/link/existing',
        type: 'POST',
        data: {
            'propertypath': objJsonMap.get(idSectionToAddTo.toString())["bo"]["estSuiviDeSection0"]["propertypath"],
            'linked_id': idSectionToAdd,
            'envkey': window.envkey
        },
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Cache-Control': 'no-cache',
            'Expires': 0
        }
    }).done(function(data3) {
        console.log('post success')
    });
}

function addAPourComposantePost(){
    $.ajax({
        url: '/due/' + 'v2/instance/' + idSectionToAddTo + '/link/existing',
        type: 'POST',
        data: {
            'propertypath': objJsonMap.get(idSectionToAddTo.toString())["bo"]["aPourComposantSection0"]["propertypath"],
            'linked_id': idSectionToAdd,
            'envkey': window.envkey
        },
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Cache-Control': 'no-cache',
            'Expires': 0
        }
    }).done(function(data3) {
        console.log('post success')
    });
}

function addAPourAlternativePost(){
    $.ajax({
        url: '/due/' + 'v2/instance/' + idSectionToAddTo + '/link/existing',
        type: 'POST',
        data: {
            'propertypath': objJsonMap.get(idSectionToAddTo.toString())["bo"]["aPourAlternativeSection0"]["propertypath"],
            'linked_id': idSectionToAdd,
            'envkey': window.envkey
        },
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Cache-Control': 'no-cache',
            'Expires': 0
        }
    }).done(function(data3) {
        console.log('post success')
    });
}

function removeAltPost(idSectionToRemove){
    $.ajax({
        url: '/due/' + 'v2/property?instance_id=' + idSectionToRemove + '&envkey=' + window.envkey + '&' +
        'propertypath='+objJsonMap.get(idSectionToRemove.toString())["bo"]["aPourAlternativeSection0"]["propertypath"],
        type: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            'Cache-Control': 'no-cache',
            'Expires': 0
        }
    }).done(function(data3) {
        console.log('delete success')
    });
}

function removeSuivPost(idSectionToRemove){
    $.ajax({
        url: '/due/' + 'v2/property?instance_id=' + idSectionToRemove + '&envkey=' + window.envkey + '&' +
        'propertypath='+objJsonMap.get(idSectionToRemove.toString())["bo"]["estSuiviDeSection0"]["propertypath"],
        type: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            'Cache-Control': 'no-cache',
            'Expires': 0
        }
    }).done(function(data3) {
        console.log('delete success')
    });
}

function removeCompPost(idSectionToRemove){
    $.ajax({
        url: '/due/' + 'v2/property?instance_id=' + idSectionToRemove + '&envkey=' + window.envkey + '&' +
        'propertypath='+objJsonMap.get(idSectionToRemove.toString())["bo"]["aPourComposantSection0"]["propertypath"],
        type: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            'Cache-Control': 'no-cache',
            'Expires': 0
        }
    }).done(function(data3) {
        console.log('delete success')
    });
}

function addPresenceIsIncluded(idSection){
    $.ajax({
        url: '/due/' + 'v2/property/include',
        type: 'POST',
        data: {
            'instance_id': idMap.get(idSection),
            'envkey': window.envkey,
            'propertypath': objJsonMap.get(idMap.get(idSection).toString())["bo"]["aPourProprieteEstPresente"]["propertypath"]
        },
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Cache-Control': 'no-cache',
            'Expires': 0
        }
    }).done(function(data3) {
        console.log('post success')
    });
}

function addPresenceIsExcluded(idSection){
    $.ajax({
        url: '/due/' + 'v2/property/exclude',
        type: 'POST',
        data: {
            'instance_id': idMap.get(idSection),
            'envkey': window.envkey,
            'propertypath': objJsonMap.get(idMap.get(idSection).toString())["bo"]["aPourProprieteEstPresente"]["propertypath"]
        },
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Cache-Control': 'no-cache',
            'Expires': 0
        }
    }).done(function(data3) {
        console.log('post success')
    });
}

function removePresenceIsIncluded(idSection){
    $.ajax({
        url: '/due/' + 'v2/property/notinclude',
        type: 'POST',
        data: {
            'instance_id': idMap.get(idSection),
            'envkey': window.envkey,
            'propertypath': objJsonMap.get(idMap.get(idSection).toString())["bo"]["aPourProprieteEstPresente"]["propertypath"]
        },
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Cache-Control': 'no-cache',
            'Expires': 0
        }
    }).done(function(data3) {
        console.log('post success')
    });
}

function removePresenceIsExcluded(idSection){
    $.ajax({
        url: '/due/' + 'v2/property/notexclude',
        type: 'POST',
        data: {
            'instance_id': idMap.get(idSection),
            'envkey': window.envkey,
            'propertypath': objJsonMap.get(idMap.get(idSection).toString())["bo"]["aPourProprieteEstPresente"]["propertypath"]
        },
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Cache-Control': 'no-cache',
            'Expires': 0
        }
    }).done(function(data3) {
        console.log('post success')
    });
}