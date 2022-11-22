package com.lenardshan.shan

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmQuery
import io.realm.kotlin.types.RealmUUID

class MainActivity : AppCompatActivity() {

    private lateinit var listView1: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView1 = findViewById(R.id.listView1)

        val actionBar = supportActionBar
        actionBar!!.title = "Person List"
        listOfPerson()
    }

    // Add Person
    @SuppressLint("InflateParams")
    private fun addPerson() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.add_person_layout, null)
        val addName = dialogLayout.findViewById<EditText>(R.id.addName)
        val addAge = dialogLayout.findViewById<EditText>(R.id.addAge)

        with(builder) {
            setTitle("Add Person")
            setPositiveButton("Add"){ _, _ ->
                val configuration = RealmConfiguration.create(schema = setOf(Person::class))
                val realm = Realm.open(configuration)
                realm.writeBlocking {
                    this.copyToRealm(Person().apply {
                        _id
                        name = addName.text.toString()
                        age = addAge.text.toString()
                    })
                }
                listOfPerson()
                addName.text = null
                addAge.text = null
                Toast.makeText(applicationContext, "Successfully added!", Toast.LENGTH_SHORT).show()
            }
            setNegativeButton("Cancel"){ _, _ ->
                //Toast.makeText(applicationContext, "Hello toast!", Toast.LENGTH_SHORT).show()
            }
            setView(dialogLayout)
            show()
        }

    }

    // Update Person
    @SuppressLint("InflateParams")
    private fun updatePerson(idPerson: String, personName: String, personAge: String) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.update_person_layout, null)
        val updateName = dialogLayout.findViewById<EditText>(R.id.updateName)
        val updateAge = dialogLayout.findViewById<EditText>(R.id.updateAge)



        with(builder) {
            updateName.setText(personName)
            updateAge.setText(personAge)
            setTitle("Edit Person")
            setPositiveButton("Update"){ _, _ ->
                val configuration = RealmConfiguration.create(schema = setOf(Person::class))
                val realm = Realm.open(configuration)

                realm.writeBlocking {
                    val person: Person? = this.query<Person>("_id == $0", RealmUUID.from(idPerson)).first().find()

                    person?.name = updateName.text.toString()
                    person?.age = updateAge.text.toString()

                }
                listOfPerson()
                Toast.makeText(applicationContext, "Updated successfully!", Toast.LENGTH_SHORT).show()
            }
            setNeutralButton("Delete"){_, _ ->
                deletePerson(idPerson = idPerson)
            }
            setNegativeButton("Cancel"){ _, _ ->
                //Toast.makeText(applicationContext, "Hello toast!", Toast.LENGTH_SHORT).show()
            }
            setView(dialogLayout)
            show()
        }

    }

    // Delete Person
    private fun deletePerson(idPerson: String) {
        val builder = AlertDialog.Builder(this)

        with(builder) {
            setTitle("Delete")
            setMessage("Are you sure to delete this item?")
            setPositiveButton("Ok") { _, _ ->
                val configuration = RealmConfiguration.create(schema = setOf(Person::class))
                val realm = Realm.open(configuration)
                realm.writeBlocking {

                    val person: RealmQuery<Person> =
                        this.query("_id == $0", RealmUUID.from(idPerson))
                    delete(person)

                }
                listOfPerson()
                Toast.makeText(applicationContext, "Deleted successfully!", Toast.LENGTH_SHORT).show()
            }
            setNegativeButton("Cancel") { _, _ ->

            }
            show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_add -> addPerson()
        }
        return super.onOptionsItemSelected(item)
    }

    // List of Person
    private fun listOfPerson() {

        val configuration = RealmConfiguration.create(schema = setOf(Person::class))
        val realm = Realm.open(configuration)
        val all2 = realm.query<Person>().find()

        // count of person
        val countOfPerson = realm.query<Person>().find().count()

//        var b = 0
//        all2.forEach { person ->
//            b++
//        }

        val final: Array<String> = Array(countOfPerson) { "" }
        val idPerson: Array<String> = Array(countOfPerson) { "" }
        val personName: Array<String> = Array(countOfPerson) { "" }
        val personAge: Array<String> = Array(countOfPerson) { "" }
        var a = 0

        all2.forEach { person ->
            val list = "Name: " + person.name.toString() + " Age: " + person.age.toString()
            val personIdentification = person._id
            personName[a] = person.name.toString()
            personAge[a] = person.age.toString()
            idPerson[a] = personIdentification.toString()
            final[a] = list
            a++
        }

        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(
            this, android.R.layout.simple_list_item_1, final
        )

        listView1.adapter = arrayAdapter

        listView1.setOnItemClickListener { _, _, position, _ ->
                updatePerson( idPerson = idPerson[position], personName = personName[position], personAge = personAge[position])
        }

    }
}