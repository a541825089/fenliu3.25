<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="租户" prop="tenantName">
        <el-input
          v-model="queryParams.tenantName"
          placeholder="请输入租户名称"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 240px">
          <el-option
            v-for="(dict, index) in dict.type.sys_normal_disable"
            :key="dict.value + '_' + index"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['system:tenant:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['system:tenant:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['system:tenant:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="tenantList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="租户ID" align="center" prop="tenantId" width="90" />
      <el-table-column label="租户名称" align="center" prop="tenantName" min-width="140" :show-overflow-tooltip="true" />
      <el-table-column label="套餐ID" align="center" prop="planId" width="90" />
      <el-table-column label="到期时间" align="center" prop="endTime" width="170">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="联系人" align="center" prop="contactName" width="120" />
      <el-table-column label="联系电话" align="center" prop="contactPhone" width="140" />
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="170">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:tenant:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-time" @click="handleRenew(scope.row)" v-hasPermi="['system:tenant:edit']">续费</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['system:tenant:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" :visible.sync="open" width="520px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="租户名称" prop="tenantName">
          <el-input v-model="form.tenantName" placeholder="请输入租户名称" />
        </el-form-item>
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="form.contactName" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="(dict, index) in dict.type.sys_normal_disable"
              :key="dict.value + '_' + index"
              :label="dict.value"
            >{{ dict.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="续费" :visible.sync="openRenew" width="520px" append-to-body>
      <el-form ref="renewForm" :model="renewForm" :rules="renewRules" label-width="90px">
        <el-form-item label="租户ID" prop="tenantId">
          <el-input v-model="renewForm.tenantId" disabled />
        </el-form-item>
        <el-form-item label="到期时间" prop="endTime">
          <el-date-picker
            v-model="renewForm.endTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择到期时间"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitRenew">确 定</el-button>
        <el-button @click="cancelRenew">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listTenant, getTenant, addTenant, updateTenant, delTenant, renewTenant } from "@/api/system/tenant";

export default {
  name: "Tenant",
  dicts: ["sys_normal_disable"],
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      tenantList: [],
      title: "",
      open: false,
      openRenew: false,
      ids: [],
      single: true,
      multiple: true,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        tenantName: null,
        status: null
      },
      form: {},
      renewForm: {},
      rules: {
        tenantName: [
          { required: true, message: "租户名称不能为空", trigger: "blur" }
        ]
      },
      renewRules: {
        endTime: [
          { required: true, message: "到期时间不能为空", trigger: "change" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      listTenant(this.queryParams).then(response => {
        this.tenantList = response.rows;
        this.total = response.total;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    cancel() {
      this.open = false;
      this.reset();
    },
    cancelRenew() {
      this.openRenew = false;
      this.resetRenew();
    },
    reset() {
      this.form = {
        tenantId: null,
        tenantName: "",
        contactName: "",
        contactPhone: "",
        status: "0",
        remark: null
      };
      this.resetForm("form");
    },
    resetRenew() {
      this.renewForm = {
        tenantId: null,
        planId: 1,
        endTime: null
      };
      this.resetForm("renewForm");
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.tenantId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "新增租户";
    },
    handleUpdate(row) {
      this.reset();
      const tenantId = row ? row.tenantId : this.ids[0];
      getTenant(tenantId).then(response => {
        this.form = response.data || response;
        if (!this.form.status) {
          this.form.status = "0";
        }
        this.open = true;
        this.title = "修改租户";
      });
    },
    handleRenew(row) {
      this.resetRenew();
      this.renewForm.tenantId = row.tenantId;
      this.renewForm.planId = row.planId || 1;
      this.renewForm.endTime = row.endTime || null;
      this.openRenew = true;
    },
    submitRenew() {
      this.$refs["renewForm"].validate(valid => {
        if (!valid) return;
        renewTenant(this.renewForm).then(() => {
          this.$modal.msgSuccess("续费成功");
          this.openRenew = false;
          this.getList();
        });
      });
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) return;
        if (this.form.tenantId != null) {
          updateTenant(this.form).then(() => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
        } else {
          addTenant(this.form).then(() => {
            this.$modal.msgSuccess("新增成功");
            this.open = false;
            this.getList();
          });
        }
      });
    },
    handleDelete(row) {
      const tenantIds = row ? row.tenantId : this.ids;
      this.$modal.confirm('是否确认删除租户编号为"' + tenantIds + '"的数据项？').then(() => {
        return delTenant(tenantIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    }
  }
};
</script>
