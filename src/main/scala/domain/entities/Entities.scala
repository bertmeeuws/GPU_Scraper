package scala.domain.entities

case class Breadcrumbs (
                         allnavid: Seq[Int],
                         breadcrumbs: String,
                         breadcrumbsdata: Seq[Breadcrumbsdata],
                         navid: Int,
                         breadcrumbsid: Int,
                         lastbreadcrumbname: String
                       )

case class Breadcrumbsdata (
                             url: String,
                             naam: String
                           )

case class RootInterface(zoek: Array[MegekkoArticle])

case class MegekkoArticle (
                         resultno: Int,
                         prodname: String,
                         prodnum: Int,
                         artikelnr: Int,
                         price: String,
                         levertijdtext: String,
                         voorraadaantal: Int,
                         voorraad: Int,
                         vvalgemeen: Int,
                         voorraadextern: Int,
                         franco: Int,
                         verwachtelevering: Int,
                         opisop: Int,
                         nietbestelbaar: Int,
                         adviesprijs: String,
                         preorder: Int,
                         verwacht: String,
                         staffel: Int,
                         safename: String,
                         breadcrumbs: Breadcrumbs,
                         uitverkocht: Int,
                         promotekst: String,
                         navid: Int,
                         link: String,
                         pricer: String
                       )