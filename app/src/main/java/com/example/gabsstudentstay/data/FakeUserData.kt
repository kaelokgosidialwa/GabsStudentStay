package com.example.gabsstudentstay.data

import java.util.Date
import java.util.UUID

fun generateFakeUID(): String {
    return UUID.randomUUID().toString().replace("-", "").take(28)
}

object FakeUserData {
    val users = listOf(
        // ─── LESSORS (15) ───
        User(userID = generateFakeUID(), email = "lessor01@gabsstay.com", username = "lessor01", name = "Thabo Mokoena", phone = "+267 71100001", role = UserRole.LESSOR.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "lessor02@gabsstay.com", username = "lessor02", name = "Kefilwe Sithole", phone = "+267 71100002", role = UserRole.LESSOR.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "lessor03@gabsstay.com", username = "lessor03", name = "Mpho Dlamini", phone = "+267 71100003", role = UserRole.LESSOR.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "lessor04@gabsstay.com", username = "lessor04", name = "Naledi Phiri", phone = "+267 71100004", role = UserRole.LESSOR.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "lessor05@gabsstay.com", username = "lessor05", name = "Goitseone Molefe", phone = "+267 71100005", role = UserRole.LESSOR.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "lessor06@gabsstay.com", username = "lessor06", name = "Boitumelo Nkosi", phone = "+267 71100006", role = UserRole.LESSOR.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "lessor07@gabsstay.com", username = "lessor07", name = "Sethunya Kgosi", phone = "+267 71100007", role = UserRole.LESSOR.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "lessor08@gabsstay.com", username = "lessor08", name = "Ditiro Seboko", phone = "+267 71100008", role = UserRole.LESSOR.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "lessor09@gabsstay.com", username = "lessor09", name = "Lorato Mothibi", phone = "+267 71100009", role = UserRole.LESSOR.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "lessor10@gabsstay.com", username = "lessor10", name = "Kagiso Segokgo", phone = "+267 71100010", role = UserRole.LESSOR.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "lessor11@gabsstay.com", username = "lessor11", name = "Onkemetse Tau", phone = "+267 71100011", role = UserRole.LESSOR.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "lessor12@gabsstay.com", username = "lessor12", name = "Refilwe Modise", phone = "+267 71100012", role = UserRole.LESSOR.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "lessor13@gabsstay.com", username = "lessor13", name = "Tshepho Gabarone", phone = "+267 71100013", role = UserRole.LESSOR.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "lessor14@gabsstay.com", username = "lessor14", name = "Bontle Makgoba", phone = "+267 71100014", role = UserRole.LESSOR.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "lessor15@gabsstay.com", username = "lessor15", name = "Phenyo Seleka", phone = "+267 71100015", role = UserRole.LESSOR.name, profileImage = "", createdAt = Date()),

        // ─── TENANTS (35) ───
        User(userID = generateFakeUID(), email = "tenant01@gabsstay.com", username = "tenant01", name = "Amantle Kgosimore", phone = "+267 72200001", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant02@gabsstay.com", username = "tenant02", name = "Bongani Nhleko", phone = "+267 72200002", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant03@gabsstay.com", username = "tenant03", name = "Charity Moyo", phone = "+267 72200003", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant04@gabsstay.com", username = "tenant04", name = "Dumisani Zwane", phone = "+267 72200004", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant05@gabsstay.com", username = "tenant05", name = "Etsile Motswana", phone = "+267 72200005", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant06@gabsstay.com", username = "tenant06", name = "Fortunate Dube", phone = "+267 72200006", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant07@gabsstay.com", username = "tenant07", name = "Gaolathe Seretse", phone = "+267 72200007", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant08@gabsstay.com", username = "tenant08", name = "Hlompho Sithole", phone = "+267 72200008", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant09@gabsstay.com", username = "tenant09", name = "Irene Molopo", phone = "+267 72200009", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant10@gabsstay.com", username = "tenant10", name = "Junior Kebonang", phone = "+267 72200010", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant11@gabsstay.com", username = "tenant11", name = "Kagiso Letshwiti", phone = "+267 72200011", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant12@gabsstay.com", username = "tenant12", name = "Lesego Mogorosi", phone = "+267 72200012", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant13@gabsstay.com", username = "tenant13", name = "Mmusi Kealotswe", phone = "+267 72200013", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant14@gabsstay.com", username = "tenant14", name = "Nametso Garebangwe", phone = "+267 72200014", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant15@gabsstay.com", username = "tenant15", name = "Onalenna Moseki", phone = "+267 72200015", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant16@gabsstay.com", username = "tenant16", name = "Pako Tshosa", phone = "+267 72200016", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant17@gabsstay.com", username = "tenant17", name = "Queen Bojang", phone = "+267 72200017", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant18@gabsstay.com", username = "tenant18", name = "Rapula Nthaga", phone = "+267 72200018", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant19@gabsstay.com", username = "tenant19", name = "Segametsi Pheto", phone = "+267 72200019", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant20@gabsstay.com", username = "tenant20", name = "Tebogo Gaobuse", phone = "+267 72200020", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant21@gabsstay.com", username = "tenant21", name = "Unity Mogapi", phone = "+267 72200021", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant22@gabsstay.com", username = "tenant22", name = "Veronica Sekgwa", phone = "+267 72200022", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant23@gabsstay.com", username = "tenant23", name = "Witness Mmolawa", phone = "+267 72200023", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant24@gabsstay.com", username = "tenant24", name = "Xolani Dlamini", phone = "+267 72200024", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant25@gabsstay.com", username = "tenant25", name = "Yolanda Serumola", phone = "+267 72200025", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant26@gabsstay.com", username = "tenant26", name = "Zandile Moswela", phone = "+267 72200026", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant27@gabsstay.com", username = "tenant27", name = "Abner Setlhare", phone = "+267 72200027", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant28@gabsstay.com", username = "tenant28", name = "Bathusi Keatlaretse", phone = "+267 72200028", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant29@gabsstay.com", username = "tenant29", name = "Cleopatra Modiegi", phone = "+267 72200029", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant30@gabsstay.com", username = "tenant30", name = "Dithato Segwabe", phone = "+267 72200030", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant31@gabsstay.com", username = "tenant31", name = "Emmanuel Moalosi", phone = "+267 72200031", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant32@gabsstay.com", username = "tenant32", name = "Faith Ramotswa", phone = "+267 72200032", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant33@gabsstay.com", username = "tenant33", name = "Gosego Rakhudu", phone = "+267 72200033", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant34@gabsstay.com", username = "tenant34", name = "Hastings Bogosi", phone = "+267 72200034", role = UserRole.TENANT.name, profileImage = "", createdAt = Date()),
        User(userID = generateFakeUID(), email = "tenant35@gabsstay.com", username = "tenant35", name = "Itumeleng Sebele", phone = "+267 72200035", role = UserRole.TENANT.name, profileImage = "", createdAt = Date())
    )
}