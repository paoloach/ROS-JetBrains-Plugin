package it.achdjian.plugin.ros.ui

import com.intellij.openapi.diagnostic.Logger
import it.achdjian.plugin.ros.settings.RosSettings
import it.achdjian.plugin.ros.settings.RosVersion
import javax.swing.event.TableModelEvent
import javax.swing.event.TableModelListener
import javax.swing.table.TableModel

class RosTablePackageModel : TableModel {
    companion object {
        private val LOG = Logger.getInstance(RosTablePackageModel::class.java)
    }

    private var modelListener = HashSet<TableModelListener>()
    private var rosVersion: RosVersion? = null

    fun updateVersions(newRosVersion: RosVersion) {
        rosVersion = newRosVersion
        newRosVersion.searchPackages()
        LOG.info("Found ${newRosVersion.packages.size}")
        modelListener.forEach {
            LOG.info("Notify table change")
            it.tableChanged(TableModelEvent(this))
        }
    }

    override fun addTableModelListener(listener: TableModelListener) {
        modelListener.add(listener)
    }

    override fun removeTableModelListener(listener: TableModelListener) {
        modelListener.remove(listener)
    }


    override fun getRowCount(): Int {
        val count = rosVersion?.packages?.size ?: 0
        return count
    }

    override fun getColumnName(colId: Int) :String{
        val name = when (colId) {
            0 -> "Name"
            1 -> "Version"
            2 -> "Description"
            else -> {
                ""
            }
        }
        LOG.info("Col $colId: $name")
        return name;
    }

    override fun isCellEditable(p0: Int, p1: Int) = false

    override fun getColumnClass(p0: Int): Class<*> = String::class.java

    override fun setValueAt(p0: Any?, p1: Int, p2: Int) {
    }

    override fun getColumnCount() = 3

    override fun getValueAt(rowIndex: Int, colIndex: Int): Any {
        val value = rosVersion?.let {
            val packages = it.packages
            if (packages.size >= rowIndex) {
                when (colIndex) {
                    0 -> packages[rowIndex]
                    else -> {
                        ""
                    }
                }
            } else {
                ""
            }
        } ?: ""
        return value
    }

}