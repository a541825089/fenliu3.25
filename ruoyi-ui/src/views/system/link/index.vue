<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="链接URL" prop="linkUrl">
        <el-input
          v-model="queryParams.linkUrl"
          placeholder="请输入链接url"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in dict.type.sys_normal_disable"
            :key="dict.value"
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
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:link:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:link:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:link:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:link:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="linkList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="链接ID" align="center" prop="linkId" />
      <el-table-column label="链接URL" align="center" prop="linkUrl" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <el-link type="primary" :href="scope.row.linkUrl" target="_blank">{{ scope.row.linkUrl }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="链接描述" align="center" prop="linkDescription" :show-overflow-tooltip="true" />
      <el-table-column label="回复语" align="center" prop="replyMsg" :show-overflow-tooltip="true" />
      <el-table-column label="随机打乱" align="center" prop="isScramble">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isScramble === '0' ? 'success' : 'info'">
            {{ scope.row.isScramble === '0' ? '开启' : '关闭' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:link:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:link:remove']"
          >删除</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-more"
            v-hasPermi="['system:link:query']"
          >更多</el-button>
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

    <!-- 添加或修改链接对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="投放国家" prop="targetCountry">
          <el-select v-model="form.targetCountry" placeholder="请选择投放国家" style="width: 100%">
            <el-option label="中国" value="CN" />
            <el-option label="美国" value="US" />
            <!-- 可以根据实际需求扩展 -->
          </el-select>
        </el-form-item>
        <el-form-item label="分流链接" prop="linkUrl">
          <el-input v-model="form.linkUrl" placeholder="请输入分流链接">
            <el-button slot="append" icon="el-icon-edit"></el-button>
          </el-input>
        </el-form-item>
        <el-form-item label="链接描述" prop="linkDescription">
          <el-input v-model="form.linkDescription" placeholder="请输入链接描述" />
        </el-form-item>
        <el-form-item label="IP防护" prop="ipProtection">
          <el-radio-group v-model="form.ipProtection">
            <el-radio label="0">开启</el-radio>
            <el-radio label="1">关闭</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="随机打乱" prop="isScramble">
          <el-radio-group v-model="form.isScramble">
            <el-radio label="0">开启</el-radio>
            <el-radio label="1">关闭</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in dict.type.sys_normal_disable"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
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
import { listLink, getLink, delLink, addLink, updateLink, exportLink } from "@/api/system/link";

export default {
  name: "Link",
  dicts: ['sys_normal_disable'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 链接表格数据
      linkList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        linkUrl: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        linkUrl: [
          { required: true, message: "分流链接不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询链接列表 */
    getList() {
      this.loading = true;
      listLink(this.queryParams).then(response => {
        this.linkList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        linkId: null,
        targetCountry: null,
        linkUrl: null,
        linkDescription: null,
        ipProtection: "1",
        isScramble: "1",
        status: "0"
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.linkId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加链接管理";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const linkId = row.linkId || this.ids
      getLink(linkId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改链接管理";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.linkId != null) {
            updateLink(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addLink(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const linkIds = row.linkId || this.ids;
      this.$modal.confirm('是否确认删除链接编号为"' + linkIds + '"的数据项？').then(function() {
        return delLink(linkIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$modal.confirm('是否确认导出所有链接数据项？').then(() => {
        return exportLink(queryParams);
      }).then(response => {
        this.download(response.msg);
      }).catch(() => {});
    }
  }
};
</script>
