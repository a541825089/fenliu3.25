<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="链接" prop="linkUrl">
        <el-input
          v-model="queryParams.linkUrl"
          placeholder="请输入链接URL"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="工单" prop="ticketNo">
        <el-input
          v-model="queryParams.ticketNo"
          placeholder="请输入工单号"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="号码" prop="numberValue">
        <el-input
          v-model="queryParams.numberValue"
          placeholder="请输入号码"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="numberType">
        <el-select v-model="queryParams.numberType" placeholder="请选择号码类型" clearable style="width: 240px">
          <el-option
            v-for="(dict, index) in dict.type.ticket_number_type"
            :key="dict.value + '_' + index"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['system:number:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['system:number:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['system:number:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['system:number:export']">导出</el-button>
      </el-col>
      <el-col :span="4" style="margin-left: 8px">
        <el-tag type="info">本页访问次数合计：{{ pageVisitTotal }}</el-tag>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="numberList"
      @selection-change="handleSelectionChange"
      show-summary
      :summary-method="getSummaries"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="号码ID" align="center" prop="numberId" width="90" />
      <el-table-column label="链接URL" align="center" prop="linkUrl" :show-overflow-tooltip="true" min-width="180">
        <template slot-scope="scope">
          <el-link v-if="scope.row.linkUrl" type="primary" :href="scope.row.linkUrl" target="_blank">{{ scope.row.linkUrl }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="工单号" align="center" prop="ticketNo" :show-overflow-tooltip="true" min-width="140" />
      <el-table-column label="号码" align="center" prop="numberValue" :show-overflow-tooltip="true" min-width="140" />
      <el-table-column label="号码类型" align="center" prop="numberType" width="120">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.ticket_number_type" :value="scope.row.numberType"/>
        </template>
      </el-table-column>
      <el-table-column label="访问次数" align="center" prop="visitCount" width="100" />
      <el-table-column label="进线人数" align="center" prop="inCount" width="100" />
      <el-table-column label="状态" align="center" width="90">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="160" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:number:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['system:number:remove']">删除</el-button>
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

    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form :key="formKey" ref="form" :model="form" :rules="rules" label-width="100px" autocomplete="off">
        <el-form-item label="号码类型" prop="numberType">
          <el-select v-model="form.numberType" placeholder="请选择号码类型" style="width: 100%">
            <el-option
              v-for="(dict, index) in dict.type.ticket_number_type"
              :key="dict.value + '_' + index"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="选择链接" prop="linkId">
          <el-select
            v-model="form.linkId"
            filterable
            remote
            reserve-keyword
            placeholder="请选择链接"
            :remote-method="remoteSearchLink"
            :loading="linkLoading"
            style="width: 100%"
            @change="handleLinkChange"
          >
            <el-option
              v-for="item in linkOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="工单号" prop="ticketNo">
          <el-input v-model="form.ticketNo" placeholder="请输入工单号" />
        </el-form-item>
        <el-form-item label="号码" prop="numberValue" v-if="!isEdit">
          <el-input
            type="textarea"
            :rows="6"
            v-model="form.numberValue"
            placeholder="一行一个号码"
          />
        </el-form-item>
        <el-form-item label="号码" prop="numberValue" v-else>
          <el-input v-model="form.numberValue" placeholder="请输入号码" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getDicts } from "@/api/system/dict/data";
import { listLink } from "@/api/system/link";
import { addNumber, changeNumberStatus, delNumber, getNumber, listNumber, updateNumber } from "@/api/system/number";

export default {
  name: "Number",
  dicts: [
    'sys_normal_disable',
    { type: 'ticket_number_type', request: (dictMeta) => getDicts(dictMeta.type).then(res => res.data) }
  ],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      numberList: [],
      title: "",
      open: false,
      isEdit: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        linkUrl: null,
        ticketNo: null,
        numberValue: null,
        numberType: null,
        status: null
      },
      form: {},
      rules: {
        numberType: [{ required: true, message: "号码类型不能为空", trigger: "change" }],
        linkId: [{ required: true, message: "请选择链接", trigger: "change" }],
        ticketNo: [{ required: true, message: "工单号不能为空", trigger: "blur" }],
        numberValue: [{ required: true, message: "号码不能为空", trigger: "blur" }]
      },
      formKey: 0,
      linkOptions: [],
      linkLoading: false,
      linkMap: {}
    };
  },
  computed: {
    pageVisitTotal() {
      return (this.numberList || []).reduce((sum, r) => sum + Number(r.visitCount || 0), 0);
    }
  },
  created() {
    this.getList();
    this.remoteSearchLink("");
  },
  methods: {
    getList() {
      this.loading = true;
      listNumber(this.queryParams)
        .then(response => {
          this.numberList = response.rows;
          this.total = response.total;
        })
        .finally(() => {
          this.loading = false;
        });
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
      this.ids = selection.map(item => item.numberId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    reset() {
      this.formKey += 1;
      this.form = {
        numberId: null,
        linkId: null,
        ticketNo: "",
        numberValue: "",
        numberType: null,
        status: "0"
      };
      this.resetForm("form");
    },
    cancel() {
      this.open = false;
      this.reset();
    },
    handleAdd() {
      this.reset();
      this.isEdit = false;
      this.open = true;
      this.title = "添加号码管理";
    },
    handleUpdate(row) {
      this.reset();
      this.isEdit = true;
      const numberId = row.numberId || this.ids;
      getNumber(numberId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改号码管理";
      });
    },
    handleDelete(row) {
      const numberIds = row.numberId || this.ids;
      this.$modal.confirm('是否确认删除号码编号为"' + numberIds + '"的数据项？').then(function() {
        return delNumber(numberIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) return;
        if (this.form.numberId != null) {
          updateNumber(this.form).then(() => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
        } else {
          addNumber(this.form).then(() => {
            this.$modal.msgSuccess("新增成功");
            this.open = false;
            this.getList();
          });
        }
      });
    },
    handleExport() {
      this.download('system/number/export', {
        ...this.queryParams
      }, `number_${new Date().getTime()}.xlsx`)
    },
    handleStatusChange(row) {
      const data = { numberId: row.numberId, status: row.status };
      changeNumberStatus(data).then(() => {
        this.$modal.msgSuccess("状态更新成功");
      }).catch(() => {
        row.status = row.status === "0" ? "1" : "0";
      });
    },
    remoteSearchLink(query) {
      this.linkLoading = true;
      listLink({ pageNum: 1, pageSize: 1000, linkUrl: query, status: "0" }).then(resp => {
        const rows = resp.rows || [];
        const map = {};
        this.linkOptions = rows.map(r => {
          map[r.linkId] = r;
          return { value: r.linkId, label: r.linkUrl };
        });
        this.linkMap = map;
        this.linkLoading = false;
      }).catch(() => {
        this.linkLoading = false;
      });
    },
    handleLinkChange(linkId) {
      const r = this.linkMap[linkId];
      if (!r) return;
      if (!this.form.ticketNo) {
        this.form.ticketNo = r.linkDescription || "";
      }
    },
    getSummaries(param) {
      const { columns, data } = param;
      const sums = [];
      columns.forEach((column, index) => {
        if (index === 0) {
          sums[index] = "合计";
          return;
        }
        if (column.property === "visitCount") {
          sums[index] = data.reduce((sum, row) => sum + Number(row.visitCount || 0), 0);
          return;
        }
        if (column.property === "inCount") {
          sums[index] = data.reduce((sum, row) => sum + Number(row.inCount || 0), 0);
          return;
        }
        sums[index] = "";
      });
      return sums;
    }
  }
};
</script>
